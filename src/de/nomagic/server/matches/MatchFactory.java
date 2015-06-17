package de.nomagic.server.matches;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.commands.DeleteMatchCommand;
import de.nomagic.commands.GetGameTypesCommand;
import de.nomagic.commands.GetMatchesCommand;
import de.nomagic.commands.JoinMatchCommand;
import de.nomagic.commands.MakeMoveCommand;
import de.nomagic.commands.RequestMatchDataCommand;
import de.nomagic.commands.Result;
import de.nomagic.commands.SendMessageCommand;
import de.nomagic.server.user.User;
import de.nomagic.server.user.UserFactory;

public class MatchFactory
{
    public static final String MatchesFileName = "matches.bin";

    private static Match[] allGameTypes = new  Match[]
    {
        new TicTacToeMatch()
    };

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private ConcurrentHashMap<String, ConcurrentHashMap<String, Match>> matches = new ConcurrentHashMap<String, ConcurrentHashMap<String, Match>>();
    private final String[] gameTypes;

    public MatchFactory()
    {
        Vector<String> list = new Vector<String>();
        for(int i = 0; i < allGameTypes.length; i++)
        {
            list.add(allGameTypes[i].getGameType());
        }
        gameTypes = list.toArray(new String[0]);
        load();
    }

    public void shutDown()
    {
        safe();
    }

    public Object[] createMatch(String gameType, String matchName, User user)
    {
        Match m = null;
        for(int i = 0; i < gameTypes.length; i++)
        {
            if(true == gameTypes[i].equals(gameType))
            {
                m = allGameTypes[i].getInstanceFor(matchName, user);
                break;
            }
        }
        if(null == m)
        {
            return new Object[] {Result.FAILED, "game type not supported"};
        }
        ConcurrentHashMap<String, Match> typesMatches = matches.get(gameType);
        if(null == typesMatches)
        {
            // first match for this type
            typesMatches = new ConcurrentHashMap<String, Match>();
        }
        else
        {
            Match old = typesMatches.get(matchName);
            if(null != old)
            {
                return new Object[] {Result.FAILED, "Match already exists"};
            }
        }
        typesMatches.put(matchName, m);
        matches.put(gameType, typesMatches);
        return new Object[] {Result.OK};
    }

    public Object[] handleGetGameTypesCommand(GetGameTypesCommand cmd)
    {
        return new Object[] {Result.DATA, gameTypes};
    }

    public Object[] getAvaliableMatches(GetMatchesCommand getCmd)
    {
        String gameType = getCmd.getGameType();
        ConcurrentHashMap<String, Match> typesMatches = matches.get(gameType);
        if(null == typesMatches)
        {
            return new Object[] {Result.FAILED, "invalid gametype"};
        }
        Iterator<String> it = typesMatches.keySet().iterator();
        Vector<String> res = new Vector<String>();
        while(it.hasNext())
        {
            String curName = it.next();
            res.add(curName);
        }
        return new Object[] {Result.DATA, res.toArray(new String[0])};
    }

    public Object[] requestMatchData(RequestMatchDataCommand cmd, User user)
    {
        ConcurrentHashMap<String, Match> typesMatches = matches.get(cmd.getGameType());
        if(null == typesMatches)
        {
            return new Object[] {Result.FAILED, "no matches for that game type"};
        }
        Match m = typesMatches.get(cmd.getMatchName());
        if(null == m)
        {
            return new Object[] {Result.FAILED, "no match with that name"};
        }
        return m.getData(cmd, user);
    }

    public Object[] makeMove(MakeMoveCommand cmd, User user, UserFactory userFactory)
    {
        ConcurrentHashMap<String, Match> typesMatches = matches.get(cmd.getGameType());
        if(null == typesMatches)
        {
            return new Object[] {Result.FAILED, "invalid gametype"};
        }
        Match m = typesMatches.get(cmd.getMatchName());
        if(null == m)
        {
            return new Object[] {Result.FAILED, "wrong match name"};
        }
        String activePlayer = m.getActivePlayer();
        if(false == activePlayer.equals(user.getName()))
        {
            return new Object[] {Result.FAILED, "it is not your turn"};
        }
        Object[] res = m.makeMove(cmd, user);
        activePlayer = m.getActivePlayer();
        if(false == activePlayer.equals(user.getName()))
        {
            // move ended
            if(true == m.isGameOngoing())
            {
                // send active Player a request to make a move
                SendMessageCommand smc = new SendMessageCommand("sendMessage "
                                                                + m.getActivePlayer() + " "
                                                                + SendMessageCommand.MOVE_TYPE + " "
                                                                + cmd.getGameType() + " "
                                                                + cmd.getMatchName() + " make your move !");
                smc.execute(user, userFactory);
            }
            // else game finished no more moves possible
        }
        return res;
    }

    public Match getMatch(String gameType, String matchName)
    {
        ConcurrentHashMap<String, Match> typesMatches = matches.get(gameType);
        if(null == typesMatches)
        {
            return null;
        }
        return typesMatches.get(matchName);
    }

    public void updateMatch(String gameType, String matchName, Match updated)
    {
        ConcurrentHashMap<String, Match> typesMatches = matches.get(gameType);
        if(null == typesMatches)
        {
            return;
        }
        typesMatches.put(matchName, updated);
    }

    public Object[] joinMatch(JoinMatchCommand cmd, User user)
    {
        ConcurrentHashMap<String, Match> typesMatches = matches.get(cmd.getGameType());
        if(null == typesMatches)
        {
            return new Object[] {Result.FAILED, "invalid game type"};
        }
        Match m = typesMatches.get(cmd.getMatchName());
        if(null == m)
        {
            return new Object[] {Result.FAILED, "wrong match name"};
        }
        return m.join(cmd, user);
    }

    public Object[] deleteMatch(DeleteMatchCommand cmd, User user)
    {
        ConcurrentHashMap<String, Match> typesMatches = matches.get(cmd.getGameType());
        if(null == typesMatches)
        {
            return new Object[] {Result.FAILED, "invalid game type"};
        }
        Match m = typesMatches.get(cmd.getMatchName());
        if(null == m)
        {
            return new Object[] {Result.FAILED, "wrong match name"};
        }
        if(   (true == user.isAdmin())                // admins may delete all matches
           || (m.getOwner().equals(user.getName())) ) // the owner may delete the match
        {
            typesMatches.remove(cmd.getMatchName());
            return new Object[] {Result.OK};
        }
        else
        {
            return new Object[] {Result.FAILED, "not allowed"};
        }
    }

    private void safe()
    {
        try
        {
            FileOutputStream fout = new FileOutputStream(MatchesFileName);
            GZIPOutputStream zipout = new GZIPOutputStream(fout);
            ObjectOutputStream oos = new ObjectOutputStream(zipout);
            oos.writeObject(matches);
            oos.flush();
            oos.close();
            zipout.close();
            fout.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void load()
    {
        try
        {
            FileInputStream fin = new FileInputStream(MatchesFileName);
            GZIPInputStream zipin = new GZIPInputStream(fin);
            ObjectInputStream ois = new ObjectInputStream(zipin);
            matches = (ConcurrentHashMap<String, ConcurrentHashMap<String, Match>>) ois.readObject();
            ois.close();
            zipin.close();
            fin.close();
        }
        catch (FileNotFoundException e)
        {
            log.info("no matches found!");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public Object[] startMatch(String gameType, String matchName, User user, UserFactory userFactory)
    {
        ConcurrentHashMap<String, Match> typesMatches = matches.get(gameType);
        if(null == typesMatches)
        {
            return new Object[] {Result.FAILED, "invalid game type"};
        }
        Match m = typesMatches.get(matchName);
        if(null == m)
        {
            return new Object[] {Result.FAILED, "wrong match name"};
        }
        Object[] res =  m.startGame(user);
        if(true == m.allStarted())
        {
            log.trace("All players started the Match {} ! Inviting {} to start !", matchName, m.getActivePlayer());
            // send active Player a request to make a move
            SendMessageCommand smc = new SendMessageCommand("sendMessage "
                                                            + m.getActivePlayer() + " "
                                                            + SendMessageCommand.MOVE_TYPE + " "
                                                            + gameType + " "
                                                            + matchName + " make your move !");
            smc.execute(user, userFactory);
        }
        return res;
    }

    public Object[] configureMatch(String gameType,
                                   String matchName,
                                   String param,
                                   String value)
    {
        ConcurrentHashMap<String, Match> typesMatches = matches.get(gameType);
        if(null == typesMatches)
        {
            return new Object[] {Result.FAILED, "invalid game type"};
        }
        Match m = typesMatches.get(matchName);
        if(null == m)
        {
            return new Object[] {Result.FAILED, "wrong match name"};
        }
        return m.configure(param, value);
    }

}
