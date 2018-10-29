package mockzebra;

interface ISocketListener
{

    public void messageGot(int messageId, String workspace, String messageFile);
}
