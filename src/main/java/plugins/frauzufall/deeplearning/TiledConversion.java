package plugins.frauzufall.deeplearning;

import icy.sequence.Sequence;

public interface TiledConversion<T> {
	/**
	 * @param input The input sequence this interface is supposed to predict in tiles
	 * @param options The options for how to handle tiling
	 * @param converter The converter used for converting input and output
	 */
	void initialize(Sequence input, TilingOptions options, Converter<T> converter);

	/**
	 * @return the input tile which has to be processed next
	 */
	T nextConvertedInput();

	/**
	 * @return whether there is another input tile to process
	 */
	boolean hasNextConvertedInput();

	/**
	 * @param result the output of the processing which will be used to assemble the output based on the input tile layout
	 */
	void resolveOutput(T result);

	/**
	 * @return the joint output of all resolved output tiles, converted back into a {@link Sequence}
	 */
	Sequence mergedOutput();
}
