package mockzebra;

import static com.esotericsoftware.minlog.Log.*;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class MockZebra
{

    private final static String VERSION = "1.0.0.0";
    private final static String MOCKZEBRA = ""
	    + "  __  __            _    _____    _                " + System.getProperty("line.separator")
	    + " |  \\/  | ___   ___| | _|__  /___| |__  _ __ __ _  " + System.getProperty("line.separator")
	    + " | |\\/| |/ _ \\ / __| |/ / / // _ | '_ \\| '__/ _` | " + System.getProperty("line.separator")
	    + " | |  | | (_) | (__|   < / /|  __| |_) | | | (_| | " + System.getProperty("line.separator")
	    + " |_|  |_|\\___/ \\___|_|\\_/____\\___|_.__/|_|  \\__,_| " + System.getProperty("line.separator")
	    + "                                                  ";

    public static void main(String[] args)
    {
	System.out.println(MOCKZEBRA);
	System.out.println(System.getProperty("line.separator") + " " + VERSION + System.getProperty("line.separator"));
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
	HelpFormatter help = new HelpFormatter();
	help.setNewLine(System.getProperty("line.separator"));
	help.printHelp("MockZebra",
		help.getNewLine()
		+ "Mock Zebra Printer!" + help.getNewLine() + help.getNewLine()
		+ "Test and view raw ZPL labels as png image or pdf document." + help.getNewLine()
		+ "Simulates zebra printer working on socket connection." + help.getNewLine()
		+ help.getNewLine(),
		getOptions(),
		help.getNewLine()
		+ "Report bugs: csonuryilmaz@gmail.com" + help.getNewLine()
		+ "Homepage: https://github.com/csonuryilmaz/MockZebra" + help.getNewLine()
		+ "Releases: https://github.com/csonuryilmaz/MockZebra/releases" + help.getNewLine()
		+ "Issues: https://github.com/csonuryilmaz/MockZebra/issues" + help.getNewLine()
		+ help.getNewLine() + "Happy coding!" + help.getNewLine(),
		true);
	System.exit(0);
    }

    private static void version()
    {
	System.out.println("Copyright (c) 2018 Onur Yılmaz");
	System.out.println("MIT License: <https://github.com/csonuryilmaz/MockZebra/blob/master/LICENSE>");
	System.out.println("This is free software: you are free to change and redistribute it.");
	System.out.println("There is NO WARRANTY, to the extent permitted by law.");
	System.exit(0);
    }

    private static void run(CommandLine cmdLine)
    {
	info("Welcome to MockZebra App!");
	Config config = new Config(getConfigFile(cmdLine));
	Socket socket = new Socket(config);
	Labelary labelary = new Labelary(config);
	labelary.listen(socket);
    }

    private static String getConfigFile(CommandLine cmdLine)
    {
	return cmdLine.hasOption("config") ? cmdLine.getOptionValue("config", "config") : "config";
    }

}
