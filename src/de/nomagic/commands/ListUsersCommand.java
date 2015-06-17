package de.nomagic.commands;

import de.nomagic.server.user.User;
import de.nomagic.server.user.UserFactory;

public class ListUsersCommand extends BaseCommand
{
    private String gameType = "";

    public ListUsersCommand()
    {
    }

    private ListUsersCommand(String line)
    {
        String[] parts = split(line);
        if(parts.length > 1)
        {
            gameType = parts[1];
        }
    }

    @Override
    public String getName()
    {
        return "listUsers";
    }

    @Override
    public Command getInstanceFor(String line)
    {
        return new ListUsersCommand(line);
    }

    @Override
    public Object[] execute(User user, UserFactory userFactory)
    {
        if(gameType.length() > 0)
        {
            return new Object[] {Result.DATA, userFactory.getListOfUsers(gameType)};
        }
        else
        {
            return new Object[] {Result.DATA, userFactory.getListOfUsers()};
        }
    }

    @Override
    public Object[] getUsageDescription()
    {
        return new Object[] {Result.DATA, getName(), "[<game type>]"};
    }

}
