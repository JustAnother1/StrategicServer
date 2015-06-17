package de.nomagic.commands;

import de.nomagic.server.matches.MatchFactory;
import de.nomagic.server.user.User;
import de.nomagic.server.user.UserFactory;

public class ConfigureMatchCommand extends BaseCommand implements Command
{
    private String gameType = "";
    private String matchName = "";
    private String param = "";
    private String value = "";

    public ConfigureMatchCommand()
    {
    }

    private ConfigureMatchCommand(String line)
    {
        String[] parts = split(line);
        if(parts.length > 4)
        {
            gameType =  parts[1];
            matchName = parts[2];
            param     = parts[3];
            value     = parts[4];
        }
    }

    public String getName()
    {
        return "cfgMatch";
    }

    public Command getInstanceFor(String line)
    {
        return new ConfigureMatchCommand(line);
    }

    public String getGameType()
    {
        return gameType;
    }

    public String getMatchName()
    {
        return matchName;
    }

    @Override
    public Object[] getUsageDescription()
    {
        return new Object[] {Result.DATA, getName(), "<game type> <match name> <parameter> <value>"};
    }

    @Override
    public Object[] execute(User user, UserFactory userFactory)
    {
        if(null == user)
        {
            return new Object[] {Result.FAILED, "login first"};
        }
        if(   (1 > gameType.length())
           || (1 > matchName.length())
           || (1 > param.length())
           || (1 > value.length()) )
        {
            return new Object[] {Result.FAILED, "Parameter missing"};
        }
        MatchFactory mf = userFactory.getMatchFactory();
        return mf.configureMatch(gameType, matchName, param, value);
    }

}
