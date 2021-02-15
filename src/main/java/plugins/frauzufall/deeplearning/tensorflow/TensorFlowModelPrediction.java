package plugins.frauzufall.deeplearning.tensorflow;

import icy.image.IcyBufferedImage;
import icy.sequence.Sequence;
import icy.type.DataType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.stat.StatUtils;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;
import org.tensorflow.proto.framework.MetaGraphDef;
import org.tensorflow.proto.framework.SignatureDef;
import plugins.frauzufall.deeplearning.AbstractModelPrediction;
import plugins.frauzufall.deeplearning.Converter;
import plugins.frauzufall.deeplearning.DefaultPredictionOptions;
import plugins.frauzufall.deeplearning.PredictionOptions;
import plugins.frauzufall.deeplearning.util.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TensorFlowModelPrediction extends AbstractModelPrediction<Tensor> {

	private final PredictionOptions options;
	private final String modelTag = "serve";
	private static final String DEFAULT_SERVING_SIGNATURE_DEF_KEY =
			"serving_default";
	private SavedModelBundle modelBundle;
	private SignatureDef signature;

	public TensorFlowModelPrediction() {
		this.options = new DefaultPredictionOptions();
	}

	public PredictionOptions getOptions() {
		return options;
	}

	@Override
	public boolean initialize() {
		try {
			modelBundle = load(getOptions().getModelFile().getAbsolutePath(), modelTag);
			signature = MetaGraphDef.parseFrom(modelBundle.metaGraphDef().toByteArray()).getSignatureDefOrThrow(
					DEFAULT_SERVING_SIGNATURE_DEF_KEY);
			System.out.println("Model inputs: " + signature.getInputsMap().toString().replace("\n", " ").replace("\t", " "));
			System.out.println("Model outputs: " + signature.getOutputsMap().toString().replace("\n", " ").replace("\t", " "));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private SavedModelBundle load(String zipFile, String modelTag) throws IOException {
		Path tmpFolder = Files.createTempDirectory(new File(zipFile).getName());
		ZipUtils.extractFolder(zipFile, tmpFolder.toAbsolutePath().toString());
		SavedModelBundle bundle = SavedModelBundle.load(tmpFolder.toAbsolutePath().toString(), modelTag);
		FileUtils.deleteDirectory(tmpFolder.toFile());
		return bundle;
	}

	@Override
	protected Tensor process(Tensor tensor) {
		Session.Runner runner = getSession().runner();
		runner.feed(getInputTensorName(), tensor);
		runner.fetch(getOutputTensorName());
		List<Tensor<?>> tensors = runner.run();
		return tensors.get(0);
	}

	@Override
	protected Converter<Tensor> createConverter() {
		return new TensorFlowConverter();
	}

	private String getInputTensorName() {
		return signature.getInputsMap().values().iterator().next().getName();
	}

	private String getOutputTensorName() {
		return signature.getOutputsMap().values().iterator().next().getName();
	}

	private Session getSession() {
		return modelBundle.session();
	}

	public Sequence predict(Sequence inputSequence) {
		return predict(inputSequence, 0);
	}

	@Override
	public Sequence predict(Sequence inputSequence, int time) {
		Sequence normalized = normalize(inputSequence, time);
		// Because the normalized sequence has only 1 time-point, we have to process its first time-point, the number 0.
		return super.predict(normalized, 0);
	}

	/**
	 * Normalizes the time-point of the specified sequence, and returns the results in a <b>1 time-point</b> sequence.
	 * @param input the sequence to normalize.
	 * @param time the time-point to normalize.
	 * @return a new sequence, with only one time-point.
	 */
	private Sequence normalize(Sequence input, int time) {
		double[] values = new double[input.getWidth() * input.getHeight()];
		for (int x = 0; x < input.getWidth(); x++) {
			for (int y = 0; y < input.getHeight(); y++) {
				values[x + y * input.getWidth()] = input.getData(time, 0, 0, y, x);
			}
		}
		double minVal = StatUtils.percentile(values, getOptions().getPercentileBottom());
		double maxVal = StatUtils.percentile(values, getOptions().getPercentileTop());
		IcyBufferedImage resImg = new IcyBufferedImage(input.getWidth(), input.getHeight(), input.getSizeC(), DataType.FLOAT);
		float[] valuesOut = new float[input.getWidth() * input.getHeight()];
		double factor = 1./(maxVal - minVal);
		for (int i = 0; i < values.length; i++) {
			valuesOut[i] = (float) ((values[i] - minVal) * factor);
		}
		Sequence res = new Sequence(resImg);
		res.setDataXY(0, 0, 0, valuesOut);
		return res;
	}
}
