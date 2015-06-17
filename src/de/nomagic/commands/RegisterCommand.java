package de.nomagic.commands;

import de.nomagic.server.user.User;
import de.nomagic.server.user.UserFactory;

public class RegisterCommand extends BaseCommand implements Command
{
    private String UserName = "";
    private String Credentials = "";
    private String Admin = "";

    public RegisterCommand()
    {
    }

    private RegisterCommand(String line)
    {
        String[] parts = split(line);
        if(parts.length > 2)
        {
            UserName = parts[1];
            Credentials = parts[2];
        }
        if(parts.length > 3)
        {
            Admin = parts[3];
        }
    }

    public String getName()
    {
        return "register";
    }

    public Command getInstanceFor(String line)
    {
        return new RegisterCommand(line);
    }

    public String getUserName()
    {
        return UserName;
    }

    public String getCredential()
    {
        return Credentials;
    }

    public String getAdmin()
    {
        return Admin;
    }

    @Override
    public Object[] getUsageDescription()
    {
        return new Object[] {Result.DATA, getName(), "<user name> <password> [Admin]"};
    }

    @Override
    public Object[] execute(User user, UserFactory userFactory)
    {
        return userFactory.register(this, user);
    }

}
