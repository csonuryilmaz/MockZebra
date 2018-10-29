package mockzebra;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static com.esotericsoftware.minlog.Log.*;

class Config
{

    private int port;

    private boolean labelSaveAsPdf;

    Config(String file)
    {
	String workspace = new File(MockZebra.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
	Properties prop = new Properties();
	if (!file.endsWith(".properties"))
	{
	    file += ".properties";
	}

	String filePath = workspace + "/" + file;
	try
	{
	    info("Reading config file: " + filePath);
	    prop.load(new FileInputStream(workspace + "/" + file));

	    setPort(prop);
	    setLabelSaveAsPdf(prop);
	}
	catch (IOException ex)
	{
	    error(file + " not found in " + workspace + " or file format is incompatible!", ex);
	}
    }

    private void setPort(Properties prop)
    {
	String socketPort = prop.getProperty("SOCKET_PORT");
	if (socketPort != null && socketPort.length() > 0)
	{
	    try
	    {
		port = Integer.parseInt(socketPort.trim());
	    }
	    catch (NumberFormatException ex)
	    {
		info("SOCKET_PORT value, " + socketPort.trim() + " from config, can't be used. Default port 1205 will be used.", ex);
		port = 1205;
	    }
	}
	else
	{
	    info("No SOCKET_PORT value from config. Default port 1205 will be used.");
	    port = 1205;
	}
    }

    int getPort()
    {
	return port;
    }

    private void setLabelSaveAsPdf(Properties prop)
    {
	String saveAs = prop.getProperty("LABEL_SAVE_AS");
	if (saveAs != null && saveAs.length() > 0)
	{
	    saveAs = saveAs.toLowerCase().trim();
	    if (saveAs.equals("pdf") || saveAs.equals("png"))
	    {
		labelSaveAsPdf = saveAs.equals("pdf");
	    }
	    else
	    {
		info("LABEL_SAVE_AS value, " + saveAs + " from config, can't be used. Default value PNG will be used.");
		labelSaveAsPdf = false;
	    }
	}
	else
	{
	    info("No LABEL_SAVE_AS value from config. Default value PNG will be used.");
	    labelSaveAsPdf = false;
	}
    }

    boolean isLabelSaveAsPdf()
    {
	return labelSaveAsPdf;
    }

}
