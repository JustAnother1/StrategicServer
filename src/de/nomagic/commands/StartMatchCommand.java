package de.nomagic.commands;

import de.nomagic.server.matches.MatchFactory;
import de.nomagic.server.user.User;
import de.nomagic.server.user.UserFactory;

public class StartMatchCommand extends BaseCommand
{
    private String gameType = "";
    private String matchName = "";

    public StartMatchCommand()
    {
    }

    public StartMatchCommand(String line)
    {
        String[] parts = split(line);
        if(parts.length > 2)
        {
            gameType = parts[1];
            matchName = parts[2];
        }
    }

    @Override
    public String getName()
    {
        return "startGame";
    }

    @Override
    public Command getInstanceFor(String line)
    {
        return new StartMatchCommand(line);
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
        return mf.startMatch(gameType, matchName, user, userFactory);
    }

}
