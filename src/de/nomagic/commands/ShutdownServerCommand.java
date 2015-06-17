package de.nomagic.commands;

import de.nomagic.server.ServerServices;
import de.nomagic.server.user.User;
import de.nomagic.server.user.UserFactory;

public class ShutdownServerCommand extends BaseCommand implements Command
{

    public ShutdownServerCommand()
    {
    }

    private ShutdownServerCommand(String line)
    {
    }

    public String getName()
    {
        return "shutdownServer";
    }

    public Command getInstanceFor(String line)
    {
        return new ShutdownServerCommand(line);
    }

    @Override
    public Object[] getUsageDescription()
    {
        return new Object[] {Result.DATA, getName()};
    }

    @Override
    public Object[] execute(User user, UserFactory userFactory)
    {
        if(false == user.isAdmin())
        {
            return new Object[] {Result.FAILED, "not allowed"};
        }
        // else :
        ServerServices server = userFactory.getServerServices();
        if(true == server.shutDownServer())
        {
            return new Object[] {Result.OK};
        }
        else
        {
             return new Object[] {Result.FAILED, "failed to shut down the server"};
        }
    }

}
