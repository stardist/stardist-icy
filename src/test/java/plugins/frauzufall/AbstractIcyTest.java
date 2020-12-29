package plugins.frauzufall;

import icy.main.Icy;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class AbstractIcyTest {

	@BeforeClass
	public static void initIcy() {
		Icy.main(new String[]{"--headless", "--nocache", "--noHLexit"});
	}

	@AfterClass
	public static void exitIcy() {
		Icy.exit(false);
	}
}
