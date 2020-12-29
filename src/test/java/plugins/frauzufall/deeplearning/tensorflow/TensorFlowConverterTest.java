package plugins.frauzufall.deeplearning.tensorflow;

import icy.gui.viewer.Viewer;
import icy.image.IcyBufferedImage;
import icy.main.Icy;
import icy.sequence.Sequence;
import icy.type.DataType;
import org.junit.Test;
import org.tensorflow.Tensor;
import plugins.frauzufall.AbstractIcyTest;
import plugins.frauzufall.deeplearning.DefaultPredictionOptions;
import plugins.frauzufall.deeplearning.DefaultTiledConversion;
import plugins.frauzufall.deeplearning.PredictionOptions;

import javax.swing.SwingUtilities;

import java.lang.reflect.InvocationTargetException;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TensorFlowConverterTest extends AbstractIcyTest {

	private Sequence input;
	private Sequence output;

	@Test
	public void testSingleTileConversion() {
		// create test input
		int width = 6;
		int height = 4;
		int channels = 2;
		IcyBufferedImage image = new IcyBufferedImage(width, height, channels, DataType.FLOAT);
		input = new Sequence(image);
		float[] valuesC1 = makeValues(0, width * height -1);
		float[] valuesC2 = makeValues(width * height, width * height * 2 - 1);
		input.getImage(0, 0).setDataXYAsFloat(0, valuesC1);
		input.getImage(0, 0).setDataXYAsFloat(1, valuesC2);
		// create conversion
		// create prediction options
		PredictionOptions options = new DefaultPredictionOptions();
		options.setBlockMultiple(5);
		// initialize conversion
		DefaultTiledConversion<Tensor> conversion = new DefaultTiledConversion<>();
		conversion.initialize(input, options, new TensorFlowConverter());
		// get first converted input entry
		Tensor next = conversion.nextConvertedInput();
		// resolve output for the input we just fetched (in this case, we just feed in the converted input)
		conversion.resolveOutput(next);
		// retrieve output of converter
		output = conversion.mergedOutput();
		// check result
		assertNotNull(output);
		assertEquals(width, output.getSizeX());
		assertEquals(height, output.getSizeY());
		assertEquals(channels, output.getSizeC());
		assertArrayEquals(valuesC1, output.getImage(0, 0).getDataXYAsFloat(0), 0.01f);
		assertArrayEquals(valuesC2, output.getImage(0, 0).getDataXYAsFloat(1), 0.01f);
	}

	private float[] makeValues(int start, int end) {
		float[] res = new float[end - start + 1];
		for (int i = start; i <= end; i++) {
			res[i-start] = i;
		}
		return res;
	}

	public static void main(String...args) throws InvocationTargetException, InterruptedException {
		Icy.main(new String[]{});
		TensorFlowConverterTest test = new TensorFlowConverterTest();
		test.testSingleTileConversion();
		SwingUtilities.invokeAndWait( () -> new Viewer( test.input ));
		SwingUtilities.invokeAndWait( () -> new Viewer( test.output ));
	}

}
