package de.nomagic.server.Pipes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.nomagic.commands.Command;
import de.nomagic.commands.CommandFactory;
import de.nomagic.commands.HelpCommand;
import de.nomagic.commands.LoginCommand;
import de.nomagic.commands.LogoutCommand;
import de.nomagic.commands.RegisterCommand;
import de.nomagic.commands.Result;
import de.nomagic.server.user.User;
import de.nomagic.server.user.UserFactory;

public class BaseConnection extends Thread implements Connection
{
    private final Logger log = LoggerFactory.getLogger(this.getClass().getName());

    private volatile OutputStream out;
    private volatile InputStream in;
    private volatile UserFactory userFactory;
    private volatile boolean shouldRun = true;
    private volatile ConnectionState state = ConnectionState.NOT_CONNECTED;
    private volatile User user;

    public BaseConnection()
    {
    }

    public BaseConnection(InputStream in, OutputStream out)
    {
        this.in = in;
        this.out = out;
        if((null != in) && (null != out))
        {
            state = ConnectionState.CONNECTING;
        }
    }

    protected void setOutputStream(OutputStream out)
    {
        this.out = out;
        if((null != in) && (null != out))
        {
            state = ConnectionState.CONNECTING;
        }
        else
        {
            state = ConnectionState.NOT_CONNECTED;
        }
    }

    protected void setInputStream(InputStream in)
    {
        this.in = in;
        if((null != in) && (null != out))
        {
            state = ConnectionState.CONNECTING;
        }
        else
        {
            state = ConnectionState.NOT_CONNECTED;
        }
    }

    public void run()
    {
        sendWelcomeMessage(out);
        while(true == shouldRun)
        {
            if(   (ConnectionState.CONNECTING == state)
               || (ConnectionState.LOGGED_IN  == state) )
            {
                sendPrompt(out);
                if(false == shouldRun) return;
                Command c = getCommand();
                if(false == shouldRun) return;
                Object[] res = handleCommand(c);
                if(false == shouldRun) return;
                writeOutResult(res, out);
            }
            else
            {
                try
                {
                    sleep(1);
                }
                catch (InterruptedException e)
                {
                    // I don't care
                }
            }
        }
    }
    protected void writeString(String res)
    {
        writeString(res, out);
    }

    protected void writeString(String res, OutputStream out2)
    {
        if(null == res)
        {
            return;
        }
        try
        {
            out2.write(res.getBytes("UTF8"));
        }
        catch (IOException e)
        {
            close();
        }
    }

    public boolean sendResponse(Object[] response)
    {
        return writeOutResult(response, out);
    }

    protected synchronized boolean writeOutResult(Object[] res, OutputStream out2)
    {
        if((null == res) || (null == out2))
        {
            return false;
        }
        if(1 >res.length)
        {
            return false;
        }
        String type = CommandFactory.getStringFor(res[0]);
        writeString(type + "\n", out2);
        if(1 < res.length)
        {
            for(int i = 1; i < res.length; i++)
            {
                Object curObj = res[i];
                if(curObj instanceof String)
                {
                    writeString((String)curObj, out2);
                }
                else if(curObj instanceof String[])
                {
                    String[] arr = (String[]) curObj;
                    if(arr.length > 0)
                    {
                        for(int k = 0; k < arr.length -1; k++)
                        {
                            writeString(arr[k], out2);
                            writeString("\n", out2);
                        }
                        writeString(arr[arr.length -1], out2);
                    }
                    else
                    {
                        writeString("[]", out2);
                    }
                }
                else
                {
                    writeString("unsupported_Data_Type", out2);
                }
                writeString("\n", out2);
            }
        }
        writeString("End\n", out2);
        return true;
    }

    private Command getCommand()
    {
        try
        {
            int read;
            StringBuffer sb = new StringBuffer();
            while(true == shouldRun)
            {
                read = in.read();
                if(-1 == read)
                {
                    // Connection closed
                    close();
                    return null;
                }
                char c = (char)read;
                if(('\n' == c) || ('\r' == c))
                {
                    if(1 < sb.length())
                    {
                        // end of Line
                        String line = sb.toString();
                        line = line.trim();
                        Command cmd = CommandFactory.getCommandFor(line);
                        return cmd;
                    }
                    // else -> was an \r\n\r or something
                }
                else
                {
                    sb.append(c);
                }
            }
        }
        catch (SocketException se)
        {
            if(   (true == se.getMessage().contains("Socket closed"))
               || (true == se.getMessage().contains("Connection reset")) )
            {
                // ok
            }
            else
            {
                se.printStackTrace();
            }
            close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            close();
        }
        return null;
    }

    protected synchronized void sendPrompt(OutputStream out2)
    {
        if(null == user)
        {
            writeString(" $ ", out2);
        }
        else
        {
            writeString("(" + user.getName() + ") $ ", out2);
        }
    }

    protected void sendWelcomeMessage(OutputStream out2)
    {
        writeString("Strategic Server Base Connection is open !\n", out2);
    }

    private Object[] handleCommand(Command cmd)
    {
        if(null == cmd)
        {
            return new Object[] {Result.FAILED, "Command was null!"};
        }
        if(ConnectionState.CONNECTING == state)
        {
            if(cmd instanceof LoginCommand)
            {
                LoginCommand lic = (LoginCommand)cmd;
                String name = lic.getUserName();
                user = userFactory.getUser(name);
                if(null == user)
                {
                    // invalid user
                    return new Object[] {Result.FAILED, "user not found"};  // security risk
                }
                else
                {
                    if(true == user.isCorrectCredential(lic.getCredential()))
                    {
                        state = ConnectionState.LOGGED_IN;
                        user.addConnection(this);
                        return new Object[] {Result.OK};
                    }
                    else
                    {
                        //invalid password
                        return new Object[] {Result.FAILED, "wrong password"};  // security risk
                    }
                }
            }
            else if(cmd instanceof RegisterCommand)
            {
                return userFactory.register(cmd);
            }
            else if(cmd instanceof HelpCommand)
            {
                HelpCommand hlpcmd = (HelpCommand) cmd;
                return handleHelpCommand(hlpcmd);
            }
            else
            {
                // this command is invalid or not allowed
                return new Object[] {Result.FAILED, "invalid command"};
            }
        }
        else if(ConnectionState.LOGGED_IN == state)
        {
            if(cmd instanceof LogoutCommand)
            {
                state = ConnectionState.CONNECTING;
                user.addConnection(null);
                user = null;
                return new Object[] {Result.OK};
            }
            else if(cmd instanceof HelpCommand)
            {
                HelpCommand hlpcmd = (HelpCommand) cmd;
                return handleHelpCommand(hlpcmd);
            }
            else
            {
                return cmd.execute(user, userFactory);
            }
        }
        else
        {
            // invalid state -> no commands possible
            return null;
        }
    }

    private Object[] handleHelpCommand(HelpCommand hlpcmd)
    {
        String helpFor = hlpcmd.getParameter();
        if(1 >helpFor.length())
        {
            if(ConnectionState.LOGGED_IN == state)
            {
                return new Object[] {Result.DATA,
                        new LogoutCommand().getName(),
                        CommandFactory.getAllCommands()};
            }
            else
            {
                return new Object[] {Result.DATA,
                                     new LoginCommand().getName(),
                                     new RegisterCommand().getName()};
            }
        }
        else
        {
            Command requestedCmd = CommandFactory.getCommandFor(helpFor);
            if(null == requestedCmd)
            {
                if(ConnectionState.LOGGED_IN == state)
                {
                    return new Object[] {Result.DATA,
                            new LogoutCommand().getName(),
                            CommandFactory.getAllCommands()};
                }
                else
                {
                    return new Object[] {Result.DATA,
                                         new LoginCommand().getName(),
                                         new RegisterCommand().getName()};
                }
            }
            else
            {
                if(ConnectionState.LOGGED_IN == state)
                {
                    return requestedCmd.getUsageDescription();
                }
                else
                {
                    if(   (requestedCmd instanceof LoginCommand)
                       || (requestedCmd instanceof RegisterCommand)
                       || (requestedCmd instanceof HelpCommand))
                    {
                        return requestedCmd.getUsageDescription();
                    }
                    else
                    {
                        return new Object[] {Result.DATA,
                                new LoginCommand().getName(),
                                new RegisterCommand().getName()};
                    }
                }
            }
        }
    }

    protected void endThread()
    {
        shouldRun = false;
    }

    public void close()
    {
        log.info("Connection Closed");
        shouldRun = false;
        try
        {
            in.close();
            out.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        this.interrupt();
    }

    public void addUserFactory(UserFactory userFactory)
    {
        this.userFactory = userFactory;
    }

    @Override
    public boolean isOpen()
    {
        return shouldRun;
    }

}
