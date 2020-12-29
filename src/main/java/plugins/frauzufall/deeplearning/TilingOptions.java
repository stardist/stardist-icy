package plugins.frauzufall.deeplearning;

public interface TilingOptions {
	void setBlockMultiple(int blockMultiple);
	void setHalo(int halo);
	int getBlockMultiple();
	void setTimePoint(int timePoint);
	int getTimePoint();
}
