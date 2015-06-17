package de.nomagic.server.matches;

import java.io.Serializable;

import de.nomagic.commands.JoinMatchCommand;
import de.nomagic.commands.MakeMoveCommand;
import de.nomagic.commands.RequestMatchDataCommand;
import de.nomagic.commands.Result;
import de.nomagic.server.user.User;

public abstract class BaseMatch implements Match, Serializable
{
    private static final long serialVersionUID = 1L;

    private String Name;
    private String owner;
    private int maxPlayer;
    private String[] players;
    private boolean[] hasStarted;
    private boolean isActive = true;
    private int activePlayer;

    public BaseMatch()
    {
    }

    public abstract Match getInstanceFor(String matchName, User user);
    public abstract String getGameType();

    protected BaseMatch(String matchName, String userName, int maxPlayer)
    {
        Name = matchName;
        owner = userName;
        this.maxPlayer = maxPlayer;
        players = new String[maxPlayer];
        hasStarted = new boolean[maxPlayer];
        for(int i = 0; i < maxPlayer; i++)
        {
            players[i] = "";
            hasStarted[i] = false;
        }
        activePlayer = getStartingPlayer();
    }

    protected int getStartingPlayer()
    {
        return 0;
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

    @Override
    public synchronized Object[] join(JoinMatchCommand cmd, User user)
    {
        for(int i = 0; i < maxPlayer; i++)
        {
            if(players[i].length() < 1)
            {
                players[i] = user.getName();
                return new Object[] {Result.OK};
            }
        }
        return new Object[] {Result.FAILED, "no free slot available"};
    }

    protected synchronized void finishedMove()
    {
        activePlayer ++;
        if(maxPlayer == activePlayer)
        {
            activePlayer = 0;
        }
    }

    protected synchronized int getActivePlayerIdx()
    {
        return activePlayer;
    }

    public synchronized String getActivePlayer()
    {
        return players[activePlayer];
    }

    @Override
    public synchronized boolean isGameOngoing()
    {
        return isActive;
    }

    protected synchronized void finishedGame()
    {
        isActive = false;
    }

    @Override
    public synchronized String getOwner()
    {
        return owner;
    }

    protected synchronized String getName()
    {
        return Name;
    }

    protected synchronized String getPlayer(int idx)
    {
        if(idx < maxPlayer)
        {
            return players[idx];
        }
        else
        {
            return "";
        }
    }

    public synchronized boolean allStarted()
    {
        for(int k = 0; k < maxPlayer; k++)
        {
            if(false == hasStarted[k])
            {
                return false;
            }
        }
        return true;
    }

    public synchronized Object[] startGame(User user)
    {
        String userName = user.getName();
        if(userName.length() < 1)
        {
            return new Object[] {Result.FAILED, "Invalid user"};
        }
        for(int i = 0; i < maxPlayer; i++)
        {
            if(true == userName.equals(players[i]))
            {
                hasStarted[i] = true;
                return new Object[] {Result.OK};
            }
        }
        return new Object[] {Result.FAILED, "user did not join the match"};
    }

}
