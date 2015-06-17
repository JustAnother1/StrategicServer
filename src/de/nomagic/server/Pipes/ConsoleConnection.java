package de.nomagic.server.Pipes;

import java.io.OutputStream;

public class ConsoleConnection extends BaseConnection
{
    public ConsoleConnection()
    {
        super(System.in, System.out);
        System.out.println("Starting Console Connection !");
    }

    @Override
    protected void sendWelcomeMessage(OutputStream out)
    {
        writeString("Strategic Server Console Connection is open !\n", out);
    }

    @Override
    public void close()
    {
        System.out.println("\nConnection Closed");
        endThread();
        this.interrupt();
    }

}
