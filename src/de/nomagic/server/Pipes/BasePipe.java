package de.nomagic.server.Pipes;

import java.util.Vector;

import de.nomagic.server.user.UserFactory;

public abstract class BasePipe extends Thread implements Pipe
{
    private volatile boolean shouldRun = true;
    private final UserFactory userFactory;
    private Vector<Connection> openConnections = new Vector<Connection>();

    public BasePipe(UserFactory userFactory)
    {
        this.userFactory = userFactory;
    }

    @Override
    public void shutDown()
    {
        shouldRun = false;
        for(int i = 0; i < openConnections.size(); i++)
        {
            Connection c = openConnections.get(i);
            c.close();
        }
        this.interrupt();
    }

    protected boolean weShallRun()
    {
        return shouldRun;
    }

    protected void addConnection(Connection con)
    {
        con.addUserFactory(userFactory);
        openConnections.addElement(con);
    }

    public void run()
    {
        waitForNewIncommingConnections();
    }

    protected abstract void waitForNewIncommingConnections();

}
