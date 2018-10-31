package mockzebra;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static com.esotericsoftware.minlog.Log.*;
import java.util.HashMap;
import java.util.HashSet;
import org.apache.commons.lang3.StringUtils;

class Config
{

    private int port;

    private boolean labelSaveAsPdf;
    private String printDensity;
    private float width;
    private float height;

    private final static HashSet<String> PRINT_DENSITY_UNITS = new HashSet<String>()
    {
	{
	    add("dpmm");
	    add("dpi");
	}
    };

    private final static HashSet<Integer> PRINT_DENSITY_DPMM_VALUES = new HashSet<Integer>()
    {
	{
	    add(6);
	    add(8);
	    add(12);
	    add(24);
	}
    };

    private final static HashSet<Integer> PRINT_DENSITY_DPI_VALUES = new HashSet<Integer>()
    {
	{
	    add(152);
	    add(203);
	    add(300);
	    add(600);
	}
    };

    private final static HashMap<Integer, Integer> PRINT_DENSITY_DPI_DPMM_MAP = new HashMap<Integer, Integer>()
    {
	{
	    put(152, 6);
	    put(203, 8);
	    put(300, 12);
	    put(600, 24);
	}
    };

    private final static HashMap<String, Float> LABEL_SIZE_UNITS = new HashMap<String, Float>()
    {
	{
	    put("inch", 1.0f);
	    put("cm", 0.393701f);
	    put("mm", 0.0393701f);
	}
    };

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
	    setPrintDensity(prop);
	    setLabelSize(prop);
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

    String getPrintDensity()
    {
	return printDensity;
    }

    private void setPrintDensity(Properties prop)
    {
	String printDensityValue = prop.getProperty("PRINT_DENSITY_VALUE");
	String printDensityUnit = prop.getProperty("PRINT_DENSITY_UNIT");

	if (StringUtils.isEmpty(printDensityValue) || StringUtils.isEmpty(printDensityUnit))
	{
	    setDefaultPrintDensity();
	}
	else
	{
	    printDensityValue = printDensityValue.trim();
	    printDensityUnit = printDensityUnit.trim().toLowerCase();
	    if (!PRINT_DENSITY_UNITS.contains(printDensityUnit))
	    {
		setDefaultPrintDensity();
	    }
	    else if (!StringUtils.isNumeric(printDensityValue))
	    {
		setDefaultPrintDensity();
	    }
	    else
	    {
		int printDensityIntValue = Integer.parseInt(printDensityValue);
		if ((printDensityUnit.equals("dpmm") && !PRINT_DENSITY_DPMM_VALUES.contains(printDensityIntValue))
			|| (printDensityUnit.equals("dpi") && !PRINT_DENSITY_DPI_VALUES.contains(printDensityIntValue)))
		{
		    setDefaultPrintDensity();
		}
		else
		{
		    if (printDensityUnit.equals("dpmm"))
		    {
			printDensity = printDensityIntValue + "dpmm";
		    }
		    else
		    {
			printDensity = PRINT_DENSITY_DPI_DPMM_MAP.get(printDensityIntValue) + "dpmm";
		    }
		}
	    }
	}
    }

    private void setDefaultPrintDensity()
    {
	info("PRINT_DENSITY_VALUE and PRINT_DENSITY_UNIT could not be read from config. Maybe empty or invalid values.");
	info("Default value 8 dpmm (203 dpi) will be used for print density.");
	info("Valid values when PRINT_DENSITY_UNIT is dpmm: \"6 dpmm\", \"8 dpmm\", \"12 dpmm\", and \"24 dpmm\"");
	info("Valid values when PRINT_DENSITY_UNIT is dpi: \"152 dpi\", \"203 dpi\", \"300 dpi\", and \"600 dpi\"");
	printDensity = "8dpmm";
    }

    float getWidth()
    {
	return width;
    }

    float getHeight()
    {
	return height;
    }

    private void setLabelSize(Properties prop)
    {
	String labelSizeUnit = prop.getProperty("LABEL_SIZE_UNIT");
	String labelSizeWidth = prop.getProperty("LABEL_SIZE_WIDTH");
	String labelSizeHeight = prop.getProperty("LABEL_SIZE_HEIGHT");

	if (StringUtils.isEmpty(labelSizeUnit) || StringUtils.isEmpty(labelSizeWidth) || StringUtils.isEmpty(labelSizeHeight))
	{
	    setDefaultLabelSize();
	}
	else
	{
	    labelSizeUnit = labelSizeUnit.trim().toLowerCase();
	    if (!LABEL_SIZE_UNITS.containsKey(labelSizeUnit))
	    {
		setDefaultLabelSize();
	    }
	    else
	    {
		labelSizeWidth = labelSizeWidth.trim();
		labelSizeHeight = labelSizeHeight.trim();

		String floatRegex = "^\\d+\\.\\d+$";
		if ((StringUtils.isNumeric(labelSizeWidth) || labelSizeWidth.matches(floatRegex))
			&& (StringUtils.isNumeric(labelSizeHeight) || labelSizeHeight.matches(floatRegex)))
		{
		    float coefficient = LABEL_SIZE_UNITS.get(labelSizeUnit);
		    width = Float.parseFloat(labelSizeWidth) * coefficient;
		    height = Float.parseFloat(labelSizeHeight) * coefficient;
		}
		else
		{
		    setDefaultLabelSize();
		}
	    }
	}
    }

    private void setDefaultLabelSize()
    {
	info("LABEL_SIZE_UNIT, LABEL_SIZE_WIDTH, and LABEL_SIZE_HEIGHT could not be read from config. Maybe empty or invalid values.");
	info("Default values; 4 inch x 6 inch will be used for width x height as label size.");
	info("Valid values for LABEL_SIZE_UNIT are \"inch\",\"cm\", and \"mm\".");
	info("Any numeric value may be used for LABEL_SIZE_WIDTH and LABEL_SIZE_HEIGHT");
	width = 4;
	height = 6;
    }

}
