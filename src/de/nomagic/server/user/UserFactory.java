package de.nomagic.server.user;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.commands.Command;
import de.nomagic.commands.DeleteUserCommand;
import de.nomagic.commands.RegisterCommand;
import de.nomagic.commands.Result;
import de.nomagic.server.ServerServices;
import de.nomagic.server.matches.MatchFactory;

public class UserFactory
{
    public static final String UserStorageFileName = "userdb.bin";

    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());
    private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<String, User>();
    private volatile ServerServices server;
    private volatile MatchFactory mf;


    public UserFactory()
    {
        load();
        if(true == users.isEmpty())
        {
            // first start - or invalid user Directory
            // -> create a new one
            log.info("no User Database found! creating new root user.");
            User RootUser = new BaseUser("root", "root", true);
            users.put("root", RootUser);
        }
    }

    @SuppressWarnings("unchecked")
    private void load()
    {
        try
        {
            FileInputStream fin = new FileInputStream(UserStorageFileName);
            GZIPInputStream zipin = new GZIPInputStream(fin);
            ObjectInputStream ois = new ObjectInputStream(zipin);
            users = (ConcurrentHashMap<String, User>) ois.readObject();
            ois.close();
            zipin.close();
            fin.close();
        }
        catch (FileNotFoundException e)
        {
            log.info("no User Database found!");
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

    public void shutDown()
    {
        try
        {
            FileOutputStream fout = new FileOutputStream(UserStorageFileName);
            GZIPOutputStream zipout = new GZIPOutputStream(fout);
            ObjectOutputStream oos = new ObjectOutputStream(zipout);
            oos.writeObject(users);
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

    public void addServerServices(ServerServices server)
    {
        this.server = server;
    }

    public ServerServices getServerServices()
    {
        return server;
    }

    public void addMatchFactory(MatchFactory mf)
    {
        this.mf = mf;
    }

    public MatchFactory getMatchFactory()
    {
        return mf;
    }


    public String[] getListOfUsers()
    {
        return users.keySet().toArray(new String[0]);
    }

    public Object getListOfUsers(String gameType)
    {
        Collection<User> uc = users.values();
        Iterator<User> it = uc.iterator();
        Vector<String> res = new Vector<String>();
        while(it.hasNext())
        {
            User u = it.next();
            if(true == u.plays(gameType))
            {
                res.add(u.getName());
            }
        }
        return res.toArray(new String[0]);
    }

    public User getUser(String name)
    {
        return users.get(name);
    }

    public Object[] register(Command cmd)
    {
        return register(cmd, null);
    }

    public Object[] register(Command cmd, User creator)
    {
        if(null == cmd)
        {
            return new Object[] {Result.FAILED};
        }
        if(cmd instanceof RegisterCommand)
        {
            RegisterCommand rcmd = (RegisterCommand) cmd;
            String Name = rcmd.getUserName();
            User user = users.get(Name);
            if(null == user)
            {
                // this user name is not registered
                String Credential = rcmd.getCredential();
                if(null != creator)
                {
                    if(   (true == creator.isAdmin())
                       && (true == "Admin".equals(rcmd.getAdmin())) )
                    {
                        user = new BaseUser(Name, Credential, true);
                    }
                    else
                    {
                        user = new BaseUser(Name, Credential);
                    }
                }
                else
                {
                    user = new BaseUser(Name, Credential);
                }
                users.put(Name, user);
                return new Object[] {Result.OK};
            }
            else
            {
                return new Object[] {Result.FAILED, "user already registered"};
            }
        }
        else
        {
            return new Object[] {Result.FAILED, "invalid command"};
        }
    }

    public Object[] deleteUser(DeleteUserCommand delcmd, User user)
    {
        String NameToDelete = delcmd.getUserName();
        if(NameToDelete.equals(user.getName()))
        {
            // suicide is allowed
            users.remove(NameToDelete);
            return new Object[] {Result.OK};
        }
        else
        {
            if(true == user.isAdmin())
            {
                // admins are allowed to delete any user
                users.remove(NameToDelete);
                return new Object[] {Result.OK};
            }
            else
            {
                return new Object[] {Result.FAILED, "not allowed"};
            }
        }
    }

    public Object[] sendMessage(String receiverName, String[] msg, User sender)
    {
        User receiver = users.get(receiverName);
        if(null == receiver)
        {
            // Message to invalid receiver
            return new Object[] {Result.FAILED};
        }
        Object[] msgResponse= new Object[] {Result.RECEIVED_MESSAGE,
                                            sender.getName(),
                                            msg};
        if(false == receiver.receiveMessage(msgResponse))
        {
            return new Object[] {Result.FAILED, "could not send message"};
        }
        else
        {
            return  new Object[] {Result.OK};
        }
    }

}
