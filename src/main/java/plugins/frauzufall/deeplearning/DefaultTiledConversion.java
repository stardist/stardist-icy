package plugins.frauzufall.deeplearning;

import icy.sequence.Sequence;

import java.util.ArrayList;
import java.util.List;

public class DefaultTiledConversion<T> implements TiledConversion<T> {

	private TilingOptions options;
	private Sequence sequence;
	private Converter<T> converter;
	private int convertedCount;
	private int maxCount = 1;
	private List<T> results;
	private int[] inputMin, inputMax, outputMin, outputMax;

	@Override
	public void initialize(Sequence input, TilingOptions options, Converter<T> converter) {
		this.sequence = input;
		this.options = options;
		this.converter = converter;
		convertedCount = 0;
		if(results != null) results.clear();
		results = new ArrayList<>();
	}

	@Override
	public T nextConvertedInput() {
		convertedCount++;
		int width = input().getWidth();
		int height = input().getHeight();
		int extendedWidth = extend(width, options().getBlockMultiple());
		int extendedHeight = extend(height, options().getBlockMultiple());
		this.inputMin = new int[]{0, 0};
		this.inputMax = new int[]{width, height};
		this.outputMin = new int[]{0, 0};
		this.outputMax = new int[]{extendedWidth, extendedHeight};
		return converter.convert(sequence, options().getTimePoint(), 0, outputMin, outputMax);
	}

	private static int extend(int realSize, int blockMultiple) {
		return (realSize / blockMultiple) * blockMultiple + blockMultiple;
	}

	@Override
	public boolean hasNextConvertedInput() {
		return convertedCount < maxCount;
	}

	@Override
	public void resolveOutput(T result) {
		results.add(result);
	}

	@Override
	public Sequence mergedOutput() {
		int[] min = new int[inputMin.length];
		int[] max = new int[inputMin.length];
		for (int i = 0; i < min.length; i++) {
			min[i] = inputMin[i] - outputMin[i];
			max[i] = inputMax[i] - outputMin[i];
		}
		return converter.convert(results.get(0), min, max);
	}

	protected TilingOptions options() {
		return options;
	}

	protected Sequence input() {
		return sequence;
	}

}
