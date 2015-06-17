package de.nomagic.commands;

import de.nomagic.server.matches.MatchFactory;
import de.nomagic.server.user.User;
import de.nomagic.server.user.UserFactory;

public class GetMatchesCommand extends BaseCommand implements Command
{
    private String GameType = "";

    public GetMatchesCommand()
    {
    }

    private GetMatchesCommand(String line)
    {
        String[] parts = split(line);
        if(parts.length > 1)
        {
            GameType = parts[1];
        }
    }

    public String getName()
    {
        return "getMatches";
    }

    public Command getInstanceFor(String line)
    {
        return new GetMatchesCommand(line);
    }

    public String getGameType()
    {
        return GameType;
    }

    @Override
    public Object[] getUsageDescription()
    {
        return new Object[] {Result.DATA, getName(), "<game type>"};
    }

    @Override
    public Object[] execute(User user, UserFactory userFactory)
    {
        MatchFactory mf = userFactory.getMatchFactory();
        return mf.getAvaliableMatches(this);
    }

}
