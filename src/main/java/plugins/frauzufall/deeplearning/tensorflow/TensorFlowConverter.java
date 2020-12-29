package plugins.frauzufall.deeplearning.tensorflow;

import icy.image.IcyBufferedImage;
import icy.sequence.Sequence;
import icy.type.DataType;
import org.tensorflow.Tensor;
import org.tensorflow.ndarray.FloatNdArray;
import org.tensorflow.ndarray.NdArrays;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.ndarray.buffer.DataBuffers;
import org.tensorflow.ndarray.buffer.FloatDataBuffer;
import org.tensorflow.types.TFloat32;
import plugins.frauzufall.deeplearning.Converter;

class TensorFlowConverter implements Converter<Tensor> {

	@Override
	public Tensor convert(Sequence sequence, int t, int z, int[] minXY, int[] maxXY) {
		System.out.println("Converting sequence to tensor");
		int width = maxXY[0] - minXY[0];
		int height = maxXY[1] - minXY[1];
		int channel = sequence.getSizeC();
		Shape shape = Shape.of(1, height, width, channel);
		final float[] imgArray = new float[width * height * channel];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				for (int c = 0; c < channel; c++) {
					float val = (float) getDataOrZero(sequence, t, z, x + minXY[0], y + minXY[1], c);
					imgArray[c+channel * (x + width * y)] = val;
				}
			}
		}

		FloatNdArray array = NdArrays.wrap(shape, DataBuffers.of(imgArray));
		return TFloat32.tensorOf(array);
	}

	@Override
	public Sequence convert(Tensor tensor, int[] minXY, int[] maxXY) {
		System.out.println("Converting tensor to sequence");
		long[] shape = tensor.shape().asArray();
		int tensorHeight = (int) shape[1];
		int tensorWidth = (int) shape[2];
		int width = maxXY[0] - minXY[0];
		int height = maxXY[1] - minXY[1];
		int channel = (int) shape[3];
		int batch = (int) shape[0];
		FloatDataBuffer floatDataBuffer = tensor.rawData().asFloats();
		IcyBufferedImage res = new IcyBufferedImage(width, height, channel, DataType.FLOAT);
		for (int x = minXY[0]; x < maxXY[0]; x++) {
			for (int y = minXY[1]; y < maxXY[1]; y++) {
				for (int c = 0; c < channel; c++) {
					float val = floatDataBuffer.getFloat(c + channel * (x + tensorWidth * y));
					res.setData(x - minXY[0], y - minXY[1], c, val);
				}
			}
		}
		return new Sequence(res);
	}

	private static double getDataOrZero(Sequence image, int t, int z, int x, int y, int c) {
		double defaultValue = 0;
		if(t >= image.getSizeT() || t < 0) return defaultValue;
		if(x >= image.getSizeX() || x < 0) return defaultValue;
		if(y >= image.getSizeY() || y < 0) return defaultValue;
		if(z >= image.getSizeZ() || z < 0) return defaultValue;
		if(c >= image.getSizeC() || c < 0) return defaultValue;
		return image.getData(t, z, c, y, x);
	}
}
