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
    private String pngViewer;
    private String pdfViewer;

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
	    info("Config File", filePath);
	    prop.load(new FileInputStream(workspace + "/" + file));

	    validatePort(prop);
	    validateLabelSaveAsPdf(prop);
	    validatePrintDensity(prop);
	    validateLabelSize(prop);
	    validatePngViewer(prop);
	    validatePdfViewer(prop);

	    setPort(prop);
	    setLabelSaveAsPdf(prop);
	    setPrintDensity(prop);
	    setLabelSize(prop);
	    setPngViewer(prop);
	    setPdfViewer(prop);
	}
	catch (IOException ex)
	{
	    warn("Config File", file + " not found in " + workspace + " or file format is incompatible!");
	    warn("Config File", ex.getMessage());
	    error("Please check out documentation at homepage for details >> https://github.com/csonuryilmaz/MockZebra");
	}
    }

    private void setPort(Properties prop)
    {
	port = Integer.parseInt(prop.getProperty("SOCKET_PORT"));
	info("SOCKET_PORT", prop.getProperty("SOCKET_PORT"));
    }

    int getPort()
    {
	return port;
    }

    private void setLabelSaveAsPdf(Properties prop)
    {
	labelSaveAsPdf = prop.getProperty("LABEL_SAVE_AS").equals("pdf");
	info("LABEL_SAVE_AS", prop.getProperty("LABEL_SAVE_AS"));
    }

    boolean isLabelSaveAsPdf()
    {
	return labelSaveAsPdf;
    }

    private void setPrintDensity(Properties prop)
    {
	printDensity = prop.getProperty("PRINT_DENSITY_UNIT").equals("dpmm")
		? Integer.parseInt(prop.getProperty("PRINT_DENSITY_VALUE")) + "dpmm"
		: PRINT_DENSITY_DPI_DPMM_MAP.get(Integer.parseInt(prop.getProperty("PRINT_DENSITY_VALUE"))) + "dpmm";
	info("PRINT_DENSITY_VALUE", prop.getProperty("PRINT_DENSITY_VALUE"));
	info("PRINT_DENSITY_UNIT", prop.getProperty("PRINT_DENSITY_UNIT"));
    }

    String getPrintDensity()
    {
	return printDensity;
    }

    private void setLabelSize(Properties prop)
    {
	float coefficient = LABEL_SIZE_UNITS.get(prop.getProperty("LABEL_SIZE_UNIT"));
	width = Float.parseFloat(prop.getProperty("LABEL_SIZE_WIDTH")) * coefficient;
	height = Float.parseFloat(prop.getProperty("LABEL_SIZE_HEIGHT")) * coefficient;
	info("LABEL_SIZE_WIDTH", prop.getProperty("LABEL_SIZE_WIDTH"));
	info("LABEL_SIZE_HEIGHT", prop.getProperty("LABEL_SIZE_HEIGHT"));
	info("LABEL_SIZE_UNIT", prop.getProperty("LABEL_SIZE_UNIT"));
    }

    float getWidth()
    {
	return width;
    }

    float getHeight()
    {
	return height;
    }

    private void setPngViewer(Properties prop)
    {
	pngViewer = prop.getProperty("VIEW_PNG_WITH");
	info("VIEW_PNG_WITH", prop.getProperty("VIEW_PNG_WITH"));
    }

    String getPngViewer()
    {
	return pngViewer;
    }

    private void setPdfViewer(Properties prop)
    {
	pdfViewer = prop.getProperty("VIEW_PDF_WITH");
	info("VIEW_PDF_WITH", prop.getProperty("VIEW_PDF_WITH"));
    }

    String getPdfViewer()
    {
	return pdfViewer;
    }

    //<editor-fold defaultstate="collapsed" desc="config file validators & default values">
    private final static String COMMENT_OUT_PARAMETER = " Comment out parameter or make it blank for default value if not sure.";

    private void validatePort(Properties prop)
    {
	String socketPort = prop.getProperty("SOCKET_PORT");
	if (StringUtils.isBlank(socketPort))
	{
	    prop.setProperty("SOCKET_PORT", "1205");
	}
	else
	{
	    socketPort = socketPort.trim();
	    if (!StringUtils.isNumeric(socketPort))
	    {
		error("SOCKET_PORT", "Nonnumeric value! Modify with correct value." + COMMENT_OUT_PARAMETER);
	    }
	    prop.setProperty("SOCKET_PORT", socketPort);
	}
    }

    private void validateLabelSaveAsPdf(Properties prop)
    {
	String labelSaveAs = prop.getProperty("LABEL_SAVE_AS");
	if (StringUtils.isBlank(labelSaveAs))
	{
	    prop.setProperty("LABEL_SAVE_AS", "png");
	}
	else
	{
	    labelSaveAs = labelSaveAs.trim().toLowerCase();
	    if (!labelSaveAs.equals("pdf") && !labelSaveAs.equals("png"))
	    {
		error("LABEL_SAVE_AS", "Invalid value! Value should be PNG or PDF." + COMMENT_OUT_PARAMETER);
	    }
	    prop.setProperty("LABEL_SAVE_AS", labelSaveAs);
	}
    }

    private void validatePrintDensity(Properties prop)
    {
	String printDensityValue = prop.getProperty("PRINT_DENSITY_VALUE");
	String printDensityUnit = prop.getProperty("PRINT_DENSITY_UNIT");

	if (StringUtils.isBlank(printDensityValue) || StringUtils.isBlank(printDensityUnit))
	{
	    prop.setProperty("PRINT_DENSITY_VALUE", "8");
	    prop.setProperty("PRINT_DENSITY_UNIT", "dpmm");
	}
	else
	{
	    printDensityValue = printDensityValue.trim();
	    printDensityUnit = printDensityUnit.trim().toLowerCase();
	    if (!PRINT_DENSITY_UNITS.contains(printDensityUnit))
	    {
		error("PRINT_DENSITY_UNIT", "Invalid value! Value should be DPMM or DPI." + COMMENT_OUT_PARAMETER);
	    }
	    if (!StringUtils.isNumeric(printDensityValue))
	    {
		error("PRINT_DENSITY_VALUE", "Nonnumeric value! Modify with correct value." + COMMENT_OUT_PARAMETER);
	    }

	    int printDensityIntValue = Integer.parseInt(printDensityValue);
	    if (printDensityUnit.equals("dpmm") && !PRINT_DENSITY_DPMM_VALUES.contains(printDensityIntValue))
	    {
		error("PRINT_DENSITY_VALUE", "Invalid value! Value should be 6,8,12 or 24 when unit is DPMM." + COMMENT_OUT_PARAMETER);
	    }
	    if (printDensityUnit.equals("dpi") && !PRINT_DENSITY_DPI_VALUES.contains(printDensityIntValue))
	    {
		error("PRINT_DENSITY_VALUE", "Invalid value! Value should be 152,203,300 or 600 when unit is DPI." + COMMENT_OUT_PARAMETER);
	    }

	    prop.setProperty("PRINT_DENSITY_VALUE", "" + printDensityIntValue);
	    prop.setProperty("PRINT_DENSITY_UNIT", printDensityUnit);
	}
    }

    private void validateLabelSize(Properties prop)
    {
	String labelSizeUnit = prop.getProperty("LABEL_SIZE_UNIT");
	String labelSizeWidth = prop.getProperty("LABEL_SIZE_WIDTH");
	String labelSizeHeight = prop.getProperty("LABEL_SIZE_HEIGHT");

	if (StringUtils.isBlank(labelSizeUnit) || StringUtils.isBlank(labelSizeWidth) || StringUtils.isBlank(labelSizeHeight))
	{
	    prop.setProperty("LABEL_SIZE_UNIT", "inch");
	    prop.setProperty("LABEL_SIZE_WIDTH", "4");
	    prop.setProperty("LABEL_SIZE_HEIGHT", "6");
	}
	else
	{
	    labelSizeUnit = labelSizeUnit.trim().toLowerCase();
	    if (!LABEL_SIZE_UNITS.containsKey(labelSizeUnit))
	    {
		error("LABEL_SIZE_UNIT", "Invalid value! Value should be INCH, CM or MM." + COMMENT_OUT_PARAMETER);
	    }
	    labelSizeWidth = labelSizeWidth.trim();
	    labelSizeHeight = labelSizeHeight.trim();
	    String floatRegex = "^\\d+\\.\\d+$";
	    if (!StringUtils.isNumeric(labelSizeWidth) && !labelSizeWidth.matches(floatRegex))
	    {
		error("LABEL_SIZE_WIDTH", "Nonnumeric value! Modify with correct value." + COMMENT_OUT_PARAMETER);
	    }
	    if (!StringUtils.isNumeric(labelSizeHeight) && !labelSizeHeight.matches(floatRegex))
	    {
		error("LABEL_SIZE_HEIGHT", "Nonnumeric value! Modify with correct value." + COMMENT_OUT_PARAMETER);
	    }
	    prop.setProperty("LABEL_SIZE_UNIT", labelSizeUnit);
	    prop.setProperty("LABEL_SIZE_WIDTH", labelSizeWidth);
	    prop.setProperty("LABEL_SIZE_HEIGHT", labelSizeHeight);
	}
    }

    private void validatePngViewer(Properties prop)
    {
	String viewPngWith = prop.getProperty("VIEW_PNG_WITH");
	if (!StringUtils.isBlank(viewPngWith))
	{
	    try
	    {
		info("Checking is PNG viewer alive? ...");
		viewPngWith = viewPngWith.trim();

		Process process = Runtime.getRuntime().exec(viewPngWith);
		if (process.isAlive())
		{
		    process.destroy();
		    info("Yes, " + viewPngWith + " can be used as PNG viewer.");
		    prop.setProperty("VIEW_PNG_WITH", viewPngWith);
		    return;
		}
	    }
	    catch (IOException ex)
	    {
		warn(ex.getMessage());
	    }
	    warn("PNG viewer has invalid command or command not working. PNG files will be saved, but won't be viewed.(silent mode)");
	}
	prop.setProperty("VIEW_PNG_WITH", "");
    }

    private void validatePdfViewer(Properties prop)
    {
	String viewPdfWith = prop.getProperty("VIEW_PDF_WITH");
	if (!StringUtils.isBlank(viewPdfWith))
	{
	    try
	    {
		info("Checking is PDF viewer alive? ...");
		viewPdfWith = viewPdfWith.trim();

		Process process = Runtime.getRuntime().exec(viewPdfWith);
		if (process.isAlive())
		{
		    process.destroy();
		    info("Yes, " + viewPdfWith + " can be used as PDF viewer.");
		    prop.setProperty("VIEW_PDF_WITH", viewPdfWith);
		    return;
		}
	    }
	    catch (IOException ex)
	    {
		warn(ex.getMessage());
	    }
	    warn("PDF viewer has invalid command or command not working. PDF files will be saved, but won't be viewed.(silent mode)");
	}
	prop.setProperty("VIEW_PDF_WITH", "");
    }

    //</editor-fold>
}
