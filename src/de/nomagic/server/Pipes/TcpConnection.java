package de.nomagic.server.Pipes;

import java.io.InputStream;
import java.io.OutputStream;

public class TcpConnection extends BaseConnection
{

    public TcpConnection(InputStream inputStream, OutputStream outputStream)
    {
        super(inputStream, outputStream);
    }

    @Override
    protected void sendWelcomeMessage(OutputStream out)
    {
        writeString("Strategic Server TCP Connection is open !\n", out);
    }

}
