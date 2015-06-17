package de.nomagic.commands;

import de.nomagic.server.matches.MatchFactory;
import de.nomagic.server.user.User;
import de.nomagic.server.user.UserFactory;

public class JoinMatchCommand extends BaseCommand implements Command
{
    private String gameType = "";
    private String matchName = "";

    public JoinMatchCommand()
    {
    }

    private JoinMatchCommand(String line)
    {
        String[] parts = split(line);
        if(parts.length > 2)
        {
            gameType = parts[1];
            matchName = parts[2];
        }
    }

    public String getName()
    {
        return "joinMatch";
    }

    public Command getInstanceFor(String line)
    {
        return new JoinMatchCommand(line);
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
        return new Object[] {Result.DATA, getName(), "<game type> <match name>"};
    }

    @Override
    public Object[] execute(User user, UserFactory userFactory)
    {
        MatchFactory mf = userFactory.getMatchFactory();
        return mf.joinMatch(this, user);
    }
}
