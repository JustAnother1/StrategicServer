package de.nomagic.server.user;

import java.io.Serializable;
import java.util.Vector;

import de.nomagic.commands.Result;
import de.nomagic.server.Pipes.Connection;

public class BaseUser implements User, Serializable
{
    private static final long serialVersionUID = 1L;
    private final String Name;
    private volatile String Credential;
    private volatile boolean isAdmin = false;
    private volatile boolean isHuman = true;
    private transient Connection con = null;
    private Vector<String> gameTypes = new Vector<String>();

    public BaseUser(String Name, String Credential)
    {
        this.Name = Name;
        this.Credential = Credential;
    }

    public BaseUser(String Name, String Credential, boolean isAdmin)
    {
        this.Name = Name;
        this.Credential = Credential;
        this.isAdmin = isAdmin;
    }

    @Override
    public boolean isCorrectCredential(String credentialToCheck)
    {
        return Credential.equals(credentialToCheck);
    }

    @Override
    public boolean isAdmin()
    {
        return isAdmin;
    }

    @Override
    public String getName()
    {
        return Name;
    }

    @Override
    public boolean receiveMessage(Object[] Response)
    {
        if((null == con) || (null == Response))
        {
            return false;
        }
        if(3 != Response.length)
        {
            return false;
        }
        if(false == Response[0].equals(Result.RECEIVED_MESSAGE))
        {
            return false;
        }
        if(false == con.isOpen())
        {
            con = null;
            return false;
        }
        return con.sendResponse(Response);
    }

    @Override
    public void addConnection(Connection con)
    {
        this.con = con;
    }

    @Override
    public synchronized String[] getGameTypes()
    {
        return gameTypes.toArray(new String[0]);
    }

    @Override
    public boolean isHuman()
    {
        return isHuman;
    }

    @Override
    public void setNewCredential(String newCredential)
    {
        Credential = newCredential;
    }

    @Override
    public void setAdmin(boolean admin)
    {
        isAdmin = admin;
    }

    @Override
    public synchronized void addGameType(String gameType)
    {
        if(false == gameTypes.contains(gameType))
        {
            gameTypes.add(gameType);
        }
    }

    @Override
    public synchronized void removeGameType(String gameType)
    {
        gameTypes.remove(gameType);
    }

    @Override
    public synchronized boolean plays(String gameType)
    {
        return gameTypes.contains(gameType);
    }

    @Override
    public void setHuman(boolean human)
    {
        isHuman = human;
    }
}
