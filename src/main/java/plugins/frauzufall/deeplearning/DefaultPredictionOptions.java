package plugins.frauzufall.deeplearning;

import java.io.File;
import java.net.URL;

public class DefaultPredictionOptions implements PredictionOptions {

	private boolean normalizeInput;
	private double percentileBottom = 1.0;
	private double percentileTop = 99.8;
	private int nTiles;
	private int blockMultiple = 64;
	private int halo;
	private int batchSize;
	private int timePoint;
	private File modelFile;
	private URL modelFileUrl;

	@Override
	public void setNormalizeInput(boolean normalizeInput) {
			this.normalizeInput = normalizeInput;
		}

	@Override
	public void setPercentileBottom(double percentileBottom) {
			this.percentileBottom = percentileBottom;
		}

	@Override
	public void setPercentileTop(double percentileTop) {
			this.percentileTop = percentileTop;
		}

	@Override
	public void setBlockMultiple(int blockMultiple) {
			this.blockMultiple = blockMultiple;
		}

	@Override
	public void setHalo(int halo) {
			this.halo = halo;
		}

	@Override
	public void setModelFile(File file) {
			this.modelFile = file;
		}

	@Override
	public void setModelURL(URL url) {
			this.modelFileUrl = url;
		}

	@Override
	public File getModelFile() {
		return modelFile;
	}

	@Override
	public void setTimePoint(int timePoint) {
		this.timePoint = timePoint;
	}

	@Override
	public int getTimePoint() {
		return timePoint;
	}

	@Override
	public int getBlockMultiple() {
		return blockMultiple;
	}

	@Override
	public double getPercentileBottom() {
		return percentileBottom;
	}

	@Override
	public double getPercentileTop() {
		return percentileTop;
	}
}
