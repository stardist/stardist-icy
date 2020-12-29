package plugins.stardist;

import de.csbdresden.stardist.AvailableModels;
import de.csbdresden.stardist.Candidates;
import de.csbdresden.stardist.Opt;
import de.csbdresden.stardist.StarDist2DModel;
import de.csbdresden.stardist.StarDist2DNMS;
import icy.file.Loader;
import icy.gui.viewer.Viewer;
import icy.main.Icy;
import icy.plugin.PluginLauncher;
import icy.plugin.PluginLoader;
import icy.sequence.Sequence;
import icy.system.thread.ThreadUtil;
import plugins.adufour.ezplug.EzButton;
import plugins.adufour.ezplug.EzGroup;
import plugins.adufour.ezplug.EzLabel;
import plugins.adufour.ezplug.EzPlug;
import plugins.adufour.ezplug.EzVarBoolean;
import plugins.adufour.ezplug.EzVarDouble;
import plugins.adufour.ezplug.EzVarEnum;
import plugins.adufour.ezplug.EzVarInteger;
import plugins.adufour.ezplug.EzVarSequence;
import plugins.frauzufall.deeplearning.ModelPrediction;
import plugins.frauzufall.deeplearning.tensorflow.TensorFlowModelPrediction;
import plugins.kernel.roi.roi2d.ROI2DPolygon;

import javax.swing.SwingUtilities;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class StarDist2DPlugin extends EzPlug
{

	private final String msgTitle = "<html>" +
			"<table><tr valign='top'><td>" +
			"<h2>Object Detection with Star-convex Shapes</h2>" +
			"<a href='https://imagej.net/StarDist'>https://imagej.net/StarDist</a>" +
			"<br/><br/><small>Please cite our paper if StarDist was helpful for your research. Thanks!</small>" +
			"</td><td>&nbsp;&nbsp;<img src='"+getResource("images/logo.png")+"' width='100' height='100'></img><td>" +
			"</tr></table>" +
			"</html>";

	private final String msgPrediction = "Neural Network Prediction";
	private final String msgPostprocessing = "Postprocessing";
	private final String msgAdvanced = "Advanced";

	private EzVarSequence input;
	private EzVarInteger excludeBoundary;
	private EzVarDouble probThresh;
	private EzVarDouble nmsThresh;
	private EzVarDouble percentileTop;
	private EzVarDouble percentileBottom;
	private EzVarBoolean normalizeInput;
	private EzVarEnum<AvailableModels.Model2D> modelChoice;

	@Override
	protected void initialize()
	{
		input = new EzVarSequence(Opt.INPUT_IMAGE);
		excludeBoundary = new EzVarInteger(Opt.EXCLUDE_BNDRY, (int) Opt.getDefault(Opt.EXCLUDE_BNDRY), 0, 1024, 1);
		probThresh = new EzVarDouble(Opt.PROB_THRESH, (double) Opt.getDefault(Opt.PROB_THRESH), 0, 1, 0.05);
		nmsThresh = new EzVarDouble(Opt.NMS_THRESH, (double) Opt.getDefault(Opt.NMS_THRESH), 0, 1, 0.05);
		percentileTop = new EzVarDouble(Opt.PERCENTILE_HIGH, (double) Opt.getDefault(Opt.PERCENTILE_HIGH), 0, 100, 0.1);
		percentileBottom = new EzVarDouble(Opt.PERCENTILE_LOW, (double) Opt.getDefault(Opt.PERCENTILE_LOW), 0, 100, 0.1);
		normalizeInput = new EzVarBoolean(Opt.NORMALIZE_IMAGE, (boolean) Opt.getDefault(Opt.NORMALIZE_IMAGE));
		modelChoice = new EzVarEnum<>(Opt.MODEL, AvailableModels.Model2D.values());

		// Add elements in order of appearance.
		addEzComponent( new EzLabel(msgTitle) );
		addEzComponent(createPredictionGroup());
		addEzComponent(createPostprocessingGroup());
		addEzComponent(createAdvancedGroup());
	}

	private EzGroup createPredictionGroup() {
		EzGroup res = new EzGroup(msgPrediction);
		res.add(input);
		input.setValue(getActiveSequence());
		res.add(modelChoice);
		res.add(normalizeInput);
		res.add(percentileBottom);
		res.add(percentileTop);
		return res;
	}

	private EzGroup createPostprocessingGroup() {
		EzGroup res = new EzGroup(msgPostprocessing);
		res.add(probThresh);
		res.add(nmsThresh);
		return res;
	}

	private EzGroup createAdvancedGroup() {
		EzButton restoreThresholds = new EzButton( Opt.SET_THRESHOLDS, l -> setThresholds() );
		EzButton restoreDefaults = new EzButton( Opt.RESTORE_DEFAULTS, l -> resetDefaults() );
		EzGroup res = new EzGroup(msgAdvanced);
		res.add(excludeBoundary);
		res.add(restoreThresholds);
		res.add(restoreDefaults);
		return res;
	}

	@Override
	public void clean()
	{
		// Nothing to do
	}

	@Override
	protected void execute()
	{

		// Load in a separate thread.
		ThreadUtil.bgRun(() -> {
			predict(input.getValue());
		});
	}

	private void predict(Sequence sequence) {
		Sequence inputSequence = input.getValue();
		ModelPrediction prediction = new TensorFlowModelPrediction();
//		if (roiPosition.equals(Opt.ROI_POSITION_AUTO))
//			roiPositionActive = input.numDimensions() > 3 && !input.isRGBMerged() ? Opt.ROI_POSITION_HYPERSTACK : Opt.ROI_POSITION_STACK;
//		else
//			roiPositionActive = roiPosition;

		File tmpModelFile = null;
		try {
			prediction.getOptions().setNormalizeInput(normalizeInput.getValue());
			prediction.getOptions().setPercentileBottom(percentileBottom.getValue());
			prediction.getOptions().setPercentileTop(percentileTop.getValue());
			prediction.getOptions().setBlockMultiple(64);

			//				case Opt.MODEL_FILE:
			//					paramsCNN.put("modelFile", modelFile);
			//					break;
			//				case Opt.MODEL_URL:
			//					paramsCNN.put("modelUrl", modelUrl);
			//					break;
			Map<String, StarDist2DModel> models = AvailableModels.get();
			String modelChoice = this.modelChoice.getValue().toString();
			final StarDist2DModel pretrainedModel = models.get(modelChoice);
			if (pretrainedModel.canGetFile()) {
				final File file = pretrainedModel.getFile();
				prediction.getOptions().setModelFile(file);
				if (pretrainedModel.isTempFile())
					tmpModelFile = file;
			} else {
				prediction.getOptions().setModelURL(pretrainedModel.url);
			}
			prediction.getOptions().setBlockMultiple(pretrainedModel.sizeDivBy);
			prediction.getOptions().setHalo(pretrainedModel.tileOverlap);
			prediction.initialize();

			StarDist2DNMS nms = new StarDist2DNMS();
			nms.setProbThresh(probThresh.getValue());
			nms.setNmsThresh(nmsThresh.getValue());
			nms.setExcludeBoundary(excludeBoundary.getValue());
//			paramsNMS.put("roiPosition", roiPositionActive);
//			paramsNMS.put("verbose", verbose);

//			final LinkedHashSet<AxisType> inputAxes = Utils.orderedAxesSet(input);
			final boolean isTimelapse = inputSequence.getSizeT() > 1;

			// TODO: option to normalize image/timelapse channel by channel or all channels jointly

			if (isTimelapse) {
				// TODO: option to normalize timelapse frame by frame (currently) or jointly
				final long numFrames = inputSequence.getSizeT();
				for (int t = 0; t < numFrames; t++) {
					Sequence predictionResult = prediction.predict(inputSequence, t);
					Candidates polygons = nms.run(predictionResult, t);
					display(sequence, polygons, t);
					getStatus().setCompletion((float)(1+t) / (float)numFrames);
				}
			} else {
				// note: the code below supports timelapse data too. differences to above:
				//       - joint normalization of all frames
				//       - requires more memory to store intermediate results (prob and dist) of all frames
				//       - allows showing prob and dist easily
				Sequence predictionResult = prediction.predict(inputSequence);
				Candidates polygons = nms.run(predictionResult, 0);
				display(sequence, polygons, 0);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (tmpModelFile != null && tmpModelFile.exists())
					tmpModelFile.delete();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void display(Sequence sequence, Candidates polygons, int time) {
		for (final int i : polygons.getWinner()) {
			final ROI2DPolygon polyRoi = polygons.getPolygonRoi(i);
			polyRoi.setT(time);
			sequence.addROI(polyRoi);
		}
	}

	private static void display(Sequence sequence) {
		try
		{
			SwingUtilities.invokeAndWait( () -> new Viewer( sequence ));
		}
		catch ( InvocationTargetException | InterruptedException e )
		{
			e.printStackTrace();
		}
	}

	private void resetDefaults()
	{
	}

	private void setThresholds() {

	}

	public static void main( final String[] args ) {
		// Launch the application.
		Icy.main( args );

		/*
		 * Programmatically launch a plugin, as if the user had clicked its
		 * button.
		 */
		String imagePath = "samples/blobs.png";
		final Sequence sequence = Loader.loadSequence( imagePath, 0, true );
		display(sequence);
		PluginLauncher.start( PluginLoader.getPlugin( StarDist2DPlugin.class.getName() ) );
	}
}
