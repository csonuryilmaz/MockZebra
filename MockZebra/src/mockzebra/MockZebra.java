package mockzebra;

import static com.esotericsoftware.minlog.Log.*;

public class MockZebra
{

    private final static String VERSION = "0.0.0.0";

    public static void main(String[] args)
    {
	setLogger(new MZLog());
	info("VERSION", VERSION);
    }

}
