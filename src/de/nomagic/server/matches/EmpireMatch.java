package de.nomagic.server.matches;

import de.nomagic.commands.MakeMoveCommand;
import de.nomagic.commands.RequestMatchDataCommand;
import de.nomagic.commands.Result;
import de.nomagic.server.user.User;

public class EmpireMatch extends BaseMatch
{
    // Map

    // Cities

    // Units


    private static final int MAX_PLAYER = 8;
    private static final long serialVersionUID = 1L;

    public EmpireMatch(String matchName, User user)
    {
        super(matchName, user.getName(), MAX_PLAYER);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Match getInstanceFor(String matchName, User user)
    {
        return new EmpireMatch(matchName, user);
    }

    @Override
    public String getGameType()
    {
        return "Empire";
    }

    @Override
    public Object[] getData(RequestMatchDataCommand cmd, User user)
    {
        return new Object[] {Result.FAILED, "not implemented"};
    }

    @Override
    public Object[] makeMove(MakeMoveCommand cmd, User user)
    {
        return new Object[] {Result.FAILED, "not implemented"};
    }


    @Override
    public Object[] configure(String parameter, String value)
    {
        // must be overridden if Match has parameters
        return new Object[] {Result.FAILED, "not implemented"};
    }

}
