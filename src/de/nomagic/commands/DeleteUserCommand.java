package de.nomagic.commands;

import de.nomagic.server.user.User;
import de.nomagic.server.user.UserFactory;

public class DeleteUserCommand extends BaseCommand implements Command
{
    private String UserName = "";

    public DeleteUserCommand()
    {
    }

    private DeleteUserCommand(String line)
    {
        String[] parts = split(line);
        if(parts.length > 1)
        {
            UserName = parts[1];
        }
    }

    public String getName()
    {
        return "deleteUser";
    }

    public Command getInstanceFor(String line)
    {
        return new DeleteUserCommand(line);
    }

    public String getUserName()
    {
        return UserName;
    }

    @Override
    public Object[] getUsageDescription()
    {
        return new Object[] {Result.DATA, getName(), "<user name>"};
    }

    @Override
    public Object[] execute(User user, UserFactory userFactory)
    {
        return userFactory.deleteUser(this, user);
    }

}
