package de.nomagic.server.Pipes;

import de.nomagic.server.user.UserFactory;

public class PipeFactory
{
    public PipeFactory()
    {
    }

    public void shutDown()
    {
    }

    public Pipe getPipe(String type, UserFactory uf)
    {
        if((null == uf) || (null == type))
        {
            return null;
        }

        if(true == "console".equals(type))
        {
            return new ConsolePipe(uf);
        }

        if(true == type.startsWith("tcp:"))
        {
            String portPart = type.substring(4);
            int port = Integer.parseInt(portPart);
            return new TcpPipe(port, uf);
        }
        // new Pipe types go here
        return null;
    }

}
