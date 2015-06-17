package de.nomagic.commands;

import de.nomagic.server.user.User;
import de.nomagic.server.user.UserFactory;

public class LogoutCommand extends BaseCommand implements Command
{

    public LogoutCommand()
    {
    }

    private LogoutCommand(String line)
    {
    }

    public String getName()
    {
        return "logout";
    }

    public Command getInstanceFor(String line)
    {
        return new LogoutCommand(line);
    }

    @Override
    public Object[] getUsageDescription()
    {
        return new Object[] {Result.DATA, getName()};
    }

    @Override
    public Object[] execute(User user, UserFactory userFactory)
    {
        return new Object[] {Result.FAILED, "Something went wrong"};
    }
}
