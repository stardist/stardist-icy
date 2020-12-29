package plugins.frauzufall.deeplearning;

import icy.sequence.Sequence;

public abstract class AbstractModelPrediction<T> implements ModelPrediction {
	@Override
	public Sequence predict(Sequence inputSequence, int time) {
		getOptions().setTimePoint(time);
		TiledConversion<T> converter = new DefaultTiledConversion<>();
		converter.initialize(inputSequence, getOptions(), createConverter());
		while(converter.hasNextConvertedInput()) {
			T tensor = converter.nextConvertedInput();
			T result = process(tensor);
			converter.resolveOutput(result);
		}
		return converter.mergedOutput();
	}
	protected abstract T process(T input);
	protected abstract Converter<T> createConverter();
}
