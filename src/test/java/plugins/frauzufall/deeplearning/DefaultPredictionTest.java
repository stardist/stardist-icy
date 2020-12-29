package plugins.frauzufall.deeplearning;

import icy.file.Loader;
import icy.gui.viewer.Viewer;
import icy.main.Icy;
import icy.sequence.Sequence;
import org.junit.Test;
import plugins.frauzufall.AbstractIcyTest;
import plugins.frauzufall.deeplearning.tensorflow.TensorFlowModelPrediction;

import javax.swing.SwingUtilities;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DefaultPredictionTest extends AbstractIcyTest {
	private Sequence input;
	private Sequence output;

	@Test
	public void runDummyPrediction() {
		// load input image
		System.out.println("Loading input image..");
		String imagePath = "samples/blobs.png";
		input = Loader.loadSequence( imagePath, 0, true );
		assertNotNull(input);

		File modelFile = new File(getClass().getResource("/dummy.zip").getFile());
		assertNotNull(modelFile);
		assertTrue(modelFile.exists());

		// setup prediction
		System.out.println("Setup prediction..");
		TensorFlowModelPrediction prediction = new TensorFlowModelPrediction();
		prediction.getOptions().setNormalizeInput(true);
		prediction.getOptions().setModelFile(modelFile);
		prediction.getOptions().setBlockMultiple(64);
		prediction.getOptions().setHalo(32);

		// initialize prediction
		System.out.println("Initialize prediction..");
		prediction.initialize();

		// predict
		System.out.println("Run prediction..");
		output = prediction.predict(input);

		// check result
		assertNotNull(output);
		output = new Sequence(output.getImage(0, 0, 0));

	}

	public static void main(String...args) throws InvocationTargetException, InterruptedException {
		Icy.main(new String[]{});
		DefaultPredictionTest test = new DefaultPredictionTest();
		test.runDummyPrediction();
		SwingUtilities.invokeAndWait( () -> new Viewer( test.input ));
		SwingUtilities.invokeAndWait( () -> new Viewer( test.output ));
	}
}
