package plugins.frauzufall.deeplearning;

import icy.image.IcyBufferedImage;
import icy.sequence.Sequence;
import icy.type.DataType;
import org.junit.Test;
import plugins.frauzufall.AbstractIcyTest;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AbstractConverterTest extends AbstractIcyTest {
	@Test
	public void testAbstractConverter() {
		// create test input
		int[] inputDims = {6, 3, 2};
		Sequence sequence = createSequence(inputDims);
		// create dummy converter
		DefaultTiledConversion<Sequence> conversion = new DefaultTiledConversion<>();
		// create prediction options
		PredictionOptions options = new DefaultPredictionOptions();
		options.setBlockMultiple(5);
		// initialize converter
		conversion.initialize(sequence, options, createDummyConverter());
		// run conversion
		assertTrue(conversion.hasNextConvertedInput());
		Sequence next = conversion.nextConvertedInput();
		assertNotNull(next);
		assertEquals(10, next.getSizeX());
		assertEquals(5, next.getSizeY());
		assertEquals(inputDims[2], next.getSizeC());
		conversion.resolveOutput(next);
		assertFalse(conversion.hasNextConvertedInput());
		// retrieve converter result
		Sequence output = conversion.mergedOutput();
		// check result
		assertNotNull(output);
		assertEquals(inputDims[0], output.getSizeX());
		assertEquals(inputDims[1], output.getSizeY());
		assertEquals(inputDims[2], output.getSizeC());
	}

	private Sequence createSequence(int[] size) {
		IcyBufferedImage image = new IcyBufferedImage(size[0], size[1], size[2], DataType.INT);
		return new Sequence(image);
	}

	private Converter<Sequence> createDummyConverter() {
		return new Converter<Sequence>(){

			@Override
			public Sequence convert(Sequence sequence, int t, int z, int[] minXY, int[] maxXY) {
				int width = maxXY[0] - minXY[0];
				int height = maxXY[1] - minXY[1];
				return new Sequence(new IcyBufferedImage(width, height, sequence.getSizeC(), sequence.getDataType_()));
			}

			@Override
			public Sequence convert(Sequence input, int[] minXY, int[] maxXY) {
				int width = maxXY[0] - minXY[0];
				int height = maxXY[1] - minXY[1];
				return new Sequence(new IcyBufferedImage(width, height, input.getSizeC(), input.getDataType_()));
			}
		};
	}

}
