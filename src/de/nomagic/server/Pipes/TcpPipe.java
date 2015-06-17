package de.nomagic.server.Pipes;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.server.user.UserFactory;

public class TcpPipe extends BasePipe
{
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private final int port;

    public TcpPipe(int port, UserFactory uf)
    {
        super(uf);
        this.port = port;
    }

    @Override
    protected void waitForNewIncommingConnections()
    {
        log.trace("Listening for TCP connections");
        ServerSocket welcomeSocket;
        try
        {
            welcomeSocket = new ServerSocket(port);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return;
        }
        while((false == isInterrupted()) && (true == weShallRun()))
        {
            try
            {
                final Socket connectionSocket = welcomeSocket.accept();
                TcpConnection con = new TcpConnection(connectionSocket.getInputStream(),
                                                      connectionSocket.getOutputStream());
                addConnection(con);
                con.start();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
        try
        {
            welcomeSocket.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

}
