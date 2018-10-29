package mockzebra;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

class Labelary
{

    private final Invocation.Builder request;
    private final String extension;

    Labelary(Config config)
    {
	Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
	WebTarget target = client.target("http://api.labelary.com/v1/printers/8dpmm/labels/4x6/0/");
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
	String zpl = "^xa^cfa,50^fo100,100^fdHello World^fs^xz";

	Response response = request.post(Entity.entity(zpl, MediaType.APPLICATION_FORM_URLENCODED));

	if (response.getStatus() == 200)
	{
	    try
	    {
		byte[] body = response.readEntity(byte[].class);
		File file = new File("label." + extension);
		Files.write(file.toPath(), body);
	    }
	    catch (IOException ex)
	    {
		Logger.getLogger(Labelary.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
	else
	{
	    String body = response.readEntity(String.class);
	    System.out.println("Error: " + body);
	}

    }

}
