package mockzebra;

import java.io.IOException;
import java.net.ServerSocket;

import static com.esotericsoftware.minlog.Log.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

class Socket
{

    private String workspace;

    Socket(Config config)
    {
	setWorkspace();
	try
	{
	    ServerSocket serverSocket = new ServerSocket(config.getPort());
	    info("Socket listening from port " + config.getPort() + " ...");
	    listen(serverSocket);
	}
	catch (IOException ex)
	{
	    error(ex.getMessage() + " " + config.getPort() + " port may be in use. Please change port from config file!");
	}
    }

    private void listen(ServerSocket serverSocket)
    {
	new Thread()
	{
	    @Override
	    public void run()
	    {
		int messageId = 0;
		while (serverSocket != null)
		{
		    try
		    {
			info("Waiting for client request ...");

			java.net.Socket socket = serverSocket.accept();
			info("Client request is accepted.");

			BufferedReader requestReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			StringBuilder buffer = new StringBuilder();
			int character;
			while ((character = requestReader.read()) != -1)
			{
			    buffer.append((char) character);
			}

			++messageId;
			info("Message is got, " + messageId + ".zpl flushing to file ...");
			saveMessage(messageId, buffer.toString());
			info("" + messageId + ".zpl saved.");
		    }
		    catch (IOException ex)
		    {
			warn(ex.getMessage());
		    }
		}
	    }
	}.start();
    }

    private void setWorkspace()
    {
	String cwd = new File(MockZebra.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd't'HHmmss");
	dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Bahrain"));
	cwd += "/" + dateFormat.format(new Date());

	File newWorkspace = new File(cwd);
	if (newWorkspace.mkdir() || newWorkspace.exists())
	{
	    info("Workspace: " + newWorkspace);
	}
	else
	{
	    error("Workspace in which ZPL, PNG and PDF files are stored could not be created!");
	}

	workspace = newWorkspace.getAbsolutePath();
    }

    private void saveMessage(int messageId, String message)
    {
	BufferedWriter fileWriter = null;
	try
	{
	    String filePath = workspace + "/" + messageId + ".zpl";
	    fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true), "UTF8"));

	    File file = new File(filePath);
	    file.setReadable(true, false);
	    file.setExecutable(true, false);
	    file.setWritable(true, false);

	    fileWriter.write(message);
	    fileWriter.flush();
	}
	catch (IOException ex)
	{
	    warn(ex.getMessage());
	}
	finally
	{
	    try
	    {
		if (fileWriter != null)
		{
		    fileWriter.close();
		}
	    }
	    catch (IOException ex)
	    {
		warn(ex.getMessage());
	    }
	}
    }

}
