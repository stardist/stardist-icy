package plugins.frauzufall.deeplearning;

import java.io.File;
import java.net.URL;

public interface PredictionOptions extends TilingOptions {
	void setNormalizeInput(boolean normalizeInput);
	void setPercentileBottom(double percentileBottom);
	void setPercentileTop(double percentileTop);
	void setModelFile(File file);
	void setModelURL(URL url);
	File getModelFile();
	double getPercentileBottom();
	double getPercentileTop();
}
