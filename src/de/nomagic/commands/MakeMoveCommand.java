package de.nomagic.commands;

import de.nomagic.server.matches.MatchFactory;
import de.nomagic.server.user.User;
import de.nomagic.server.user.UserFactory;

public class MakeMoveCommand extends BaseCommand implements Command
{
    private final static int GAME_PARAMETER_OFFSET = 3;
    private String gameType = "";
    private String matchName = "";
    private String[] parts = new String[0];

    public MakeMoveCommand()
    {
    }

    private MakeMoveCommand(String line)
    {
        parts = split(line);
        if(parts.length > 2)
        {
            // parts[0] is the command name
            gameType = parts[1];
            matchName = parts[2];
        }
    }

    public String getName()
    {
        return "makeMove";
    }

    public Command getInstanceFor(String line)
    {
        return new MakeMoveCommand(line);
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
        return mf.makeMove(this, user, userFactory);
    }

    public int getParameterAsInt(int idx)
    {
        if(parts.length > GAME_PARAMETER_OFFSET + idx)
        {
            int res = -1;
            try
            {
                res = Integer.parseInt(parts[GAME_PARAMETER_OFFSET + idx]);
            }
            catch(NumberFormatException e)
            {
                res = -2;
            }
            return res;
        }
        else
        {
            return -3;
        }
    }

}
