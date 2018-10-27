package mockzebra;

import static com.esotericsoftware.minlog.Log.*;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class MockZebra
{

    private final static String VERSION = "0.1.0.0";

    public static void main(String[] args)
    {
	setLogger(new MZLog());
	CommandLine cmdLine = getParser(getOptions(), args);
	if (cmdLine.hasOption("help"))
	{
	    help();
	}

	if (cmdLine.hasOption("version"))
	{
	    version();
	}

	run(cmdLine);
    }

    private static Options getOptions()
    {
	Options options = new Options();

	options.addOption("h", "help", false, "Prints this help message.");
	options.addOption("v", "version", false, "Prints detailed version information.");
	options.addOption("c", "config", true, "Lists all operation files in workspace.");

	return options;
    }

    private static CommandLine getParser(Options options, String[] args)
    {
	try
	{
	    CommandLineParser parser = new DefaultParser();
	    return parser.parse(options, args);
	}
	catch (ParseException ex)
	{
	    error("Parsing command line arguments failed!", ex);
	}
	return null;
    }

    private static void help()
    {
	warn("(@todo) help arg not implemented yet!");

	System.exit(0);
    }

    private static void version()
    {
	System.out.println(VERSION);
	System.exit(0);
    }

    private static void run(CommandLine cmdLine)
    {
	info("Welcome to MockZebra App! (v " + VERSION + ")");

	Config config = new Config(getConfigFile(cmdLine));
	System.out.println(config.getPort());

    }

    private static String getConfigFile(CommandLine cmdLine)
    {
	return cmdLine.hasOption("config") ? cmdLine.getOptionValue("config", "config") : "config";
    }

}
