package de.nomagic.commands;

import de.nomagic.server.matches.MatchFactory;
import de.nomagic.server.user.User;
import de.nomagic.server.user.UserFactory;

public class GetGameTypesCommand extends BaseCommand implements Command
{
    public GetGameTypesCommand()
    {
    }

    private GetGameTypesCommand(String line)
    {
    }

    public String getName()
    {
        return "getGameTypes";
    }

    public Command getInstanceFor(String line)
    {
        return new GetGameTypesCommand(line);
    }

    @Override
    public Object[] getUsageDescription()
    {
        return new Object[] {Result.DATA, getName()};
    }

    @Override
    public Object[] execute(User user, UserFactory userFactory)
    {
        MatchFactory mf = userFactory.getMatchFactory();
        return mf.handleGetGameTypesCommand(this);
    }
}
