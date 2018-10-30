package mockzebra;

import static com.esotericsoftware.minlog.Log.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

class Labelary implements ISocketListener
{

    private final Invocation.Builder request;
    private final String extension;

    Labelary(Config config)
    {
	Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
	WebTarget target = client.target("http://api.labelary.com/v1/printers/" + config.getPrintDensity() + "/labels/4x6/0/");
	// adjust print density (8dpmm), label width (4 inches), label height (6 inches), and label index (0) as necessary
	request = target.request();
	if (config.isLabelSaveAsPdf())
	{
	    request.accept("application/pdf");
	    extension = "pdf";
	}
	else
	{
	    request.accept("image/png");
	    extension = "png";
	}
    }

    void listen(Socket socket)
    {
	socket.setListener(this);
    }

    @Override
    public void messageGot(int messageId, String workspace, String zplFile)
    {
	try
	{
	    String zpl = getZpl(workspace, zplFile, StandardCharsets.UTF_8);
	    info("Rendering " + zplFile + " ...");
	    Response response = request.post(Entity.entity(zpl, MediaType.APPLICATION_FORM_URLENCODED));
	    if (response.getStatus() == 200)
	    {
		info("Success.");
		saveLabel(workspace, messageId, response);
	    }
	    else
	    {
		String body = response.readEntity(String.class);
		System.out.println("Error: " + body);
	    }
	}
	catch (IOException ex)
	{
	    warn("Zpl content could not be read from file:" + workspace + "/" + zplFile);
	    warn(zplFile + ":" + ex.getMessage());
	}
    }

    private String getZpl(String workspace, String zplFile, Charset encoding) throws IOException
    {
	byte[] encoded = Files.readAllBytes(Paths.get(workspace + "/" + zplFile));
	return new String(encoded, encoding);
    }

    private void saveLabel(String workspace, int messageId, Response response)
    {
	String labelPath = workspace + "/" + messageId + "." + extension;
	try
	{
	    byte[] body = response.readEntity(byte[].class);
	    File file = new File(labelPath);
	    Files.write(file.toPath(), body);
	    info("Label saved as " + messageId + "." + extension + " in workspace.");
	}
	catch (IOException ex)
	{
	    warn("Label could not be saved as:" + labelPath);
	    warn(ex.getMessage());
	}
    }

}
