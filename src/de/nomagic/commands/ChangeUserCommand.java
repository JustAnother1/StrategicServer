package de.nomagic.commands;

import de.nomagic.server.user.User;
import de.nomagic.server.user.UserFactory;

public class ChangeUserCommand extends BaseCommand
{
    private String userName = "";
    private String param = "";
    private String gameType = "";
    private String value = "";

    public ChangeUserCommand()
    {
    }

    public ChangeUserCommand(String line)
    {
        String[] parts = split(line);
        if(parts.length > 3)
        {
            userName = parts[1];
            param = parts[2];
            if(parts.length > 4)
            {
                gameType = parts[3];
                value = parts[4];
            }
            else
            {
                value = parts[3];
            }
        }
    }

    @Override
    public String getName()
    {
        return "changeUser";
    }

    @Override
    public Command getInstanceFor(String line)
    {
        return new ChangeUserCommand(line);
    }

    @Override
    public Object[] execute(User user, UserFactory userFactory)
    {
        if(null == user)
        {
            return new Object[] {Result.FAILED, "login first"};
        }
        if((1 > userName.length()) || (1 > param.length()) || (1 > value.length()))
        {
            return new Object[] {Result.FAILED, "Parameter missing"};
        }
        if(userName.equals(user.getName()))
        {
            // user changes himself -> OK
        }
        else
        {
            // only admins may change other users
            if(true == user.isAdmin())
            {
                // is admin -> ok
            }
            else
            {
                return new Object[] {Result.FAILED, "not allowed !"};
            }
        }
        User modifiedUser = userFactory.getUser(userName);
        boolean bValue = getBoolFromString(value);
        if("human".equals(param))
        {
            modifiedUser.setHuman(bValue);
        }
        else if("admin".equals(param))
        {
            modifiedUser.setAdmin(bValue);
        }
        else if("plays".equals(param))
        {
            if(true == bValue)
            {
                modifiedUser.addGameType(gameType);
            }
            else
            {
                modifiedUser.removeGameType(gameType);
            }
        }
        else
        {
            return new Object[] {Result.FAILED, "wrong parameter"};
        }
        return new Object[] {Result.OK};
    }

    private boolean getBoolFromString(String val)
    {
        if(true == "yes".equals(val))
        {
            return true;
        }
        return false;
    }

    @Override
    public Object[] getUsageDescription()
    {
        return new Object[] {Result.DATA, getName(),
                "<user name> <parameter to change> <new value>",
                "Parameter can be:",
                "human - yes/no",
                "admin - yes/no",
                "plays <gameType> - yes/no"};
    }
}
