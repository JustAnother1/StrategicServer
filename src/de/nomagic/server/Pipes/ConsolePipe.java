package de.nomagic.server.Pipes;

import de.nomagic.server.user.UserFactory;

public class ConsolePipe extends BasePipe
{
    public ConsolePipe(UserFactory userFactory)
    {
        super(userFactory);
        ConsoleConnection con = new ConsoleConnection();
        addConnection(con);
        con.start();
    }

    @Override
    protected void waitForNewIncommingConnections()
    {
        // nothing to do here
    }
}
