package de.nomagic.commands;

import de.nomagic.server.user.User;
import de.nomagic.server.user.UserFactory;

public class HelpCommand extends BaseCommand implements Command
{
    private String parameter = "";

    public HelpCommand()
    {
    }

    private HelpCommand(String line)
    {
        String[] parts = split(line);
        if(parts.length > 1)
        {
            parameter = parts[1];
        }
    }

    public String getName()
    {
        return "help";
    }

    public Command getInstanceFor(String line)
    {
        return new HelpCommand(line);
    }

    public String getParameter()
    {
        return parameter;
    }

    @Override
    public Object[] getUsageDescription()
    {
        return new Object[] {Result.DATA, getName(), "[command name]"};
    }

    @Override
    public Object[] execute(User user, UserFactory userFactory)
    {
        return new Object[] {Result.FAILED, "something went wrong"};
    }

}
