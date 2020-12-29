package plugins.frauzufall.deeplearning;

import icy.sequence.Sequence;

public interface Converter<T> {
	T convert(Sequence sequence, int t, int z, int[]minXY, int[] maxXY);
	Sequence convert(T input, int[] minXY, int[] maxXY);
}
