package de.nomagic.commands;

import de.nomagic.server.user.User;
import de.nomagic.server.user.UserFactory;

public class LoginCommand extends BaseCommand implements Command
{
    private String UserName = "";
    private String Credentials = "";

    public LoginCommand()
    {
    }

    private LoginCommand(String line)
    {
        String[] parts = split(line);
        if(parts.length > 2)
        {
            UserName = parts[1];
            Credentials = parts[2];
        }
    }

    public String getName()
    {
        return "login";
    }

    public Command getInstanceFor(String line)
    {
        return new LoginCommand(line);
    }

    public String getUserName()
    {
        return UserName;
    }

    public String getCredential()
    {
        return Credentials;
    }

    @Override
    public Object[] getUsageDescription()
    {
        return new Object[] {Result.DATA, getName(), "<user name> <password>"};
    }

    @Override
    public Object[] execute(User user, UserFactory userFactory)
    {
        return new Object[] {Result.FAILED, "something went wrong"};
    }

}
