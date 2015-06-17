package de.nomagic.commands;

import de.nomagic.server.user.User;
import de.nomagic.server.user.UserFactory;

public class ShowUserDetailsCommand extends BaseCommand {

    private String UserName = "";

    public ShowUserDetailsCommand()
    {
    }

    public ShowUserDetailsCommand(String line)
    {
        String[] parts = split(line);
        if(parts.length > 1)
        {
            UserName = parts[1];
        }
    }

    @Override
    public String getName()
    {
        return "showUserDetails";
    }

    @Override
    public Command getInstanceFor(String line)
    {
        return new ShowUserDetailsCommand(line);
    }

    @Override
    public Object[] getUsageDescription()
    {
        return new Object[] {Result.DATA, getName(), "<user name>"};
    }

    @Override
    public Object[] execute(User user, UserFactory userFactory)
    {
        User u = userFactory.getUser(UserName);
        if(null == u)
        {
            return new Object[] {Result.FAILED, "unknown user"};
        }
        return new Object[] {Result.DATA,
                             "admin : " + u.isAdmin(),
                             "human : " + u.isAdmin(),
                             "plays:",
                             u.getGameTypes()};
    }

}
