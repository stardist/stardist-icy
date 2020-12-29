package plugins.frauzufall.deeplearning;

import icy.sequence.Sequence;

public interface ModelPrediction {
	PredictionOptions getOptions();
	boolean initialize();
	Sequence predict(Sequence inputSequence, int time);
	default Sequence predict(Sequence inputSequence) {
		return predict(inputSequence, 0);
	}
}
