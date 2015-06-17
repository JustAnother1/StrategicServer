package de.nomagic.commands;

import java.util.Vector;

public final class CommandFactory
{

    private CommandFactory()
    {
    }

    private static Command[] allCommands = new Command[] {
     // Matches
        new RequestMatchDataCommand(),
        new MakeMoveCommand(),
        new ConfigureMatchCommand(),
        new JoinMatchCommand(),
        new CreateMatchCommand(),
        new DeleteMatchCommand(),
        new StartMatchCommand(),
     // user
        new LoginCommand(),
        new SendMessageCommand(),
        new RegisterCommand(),
        new LogoutCommand(),
        new DeleteUserCommand(),
        new ListUsersCommand(),
        new ChangeUserCommand(),
        new ShowUserDetailsCommand(),
     // Server
        new GetMatchesCommand(),
        new GetGameTypesCommand(),
        new ShutdownServerCommand(),
        new HelpCommand()
    };

    public static Command getCommandFor(String line)
    {
        if(null == line)
        {
            return null;
        }
        for(int i = 0; i < allCommands.length; i++)
        {
            if(true == line.startsWith(allCommands[i].getName()))
            {
                return allCommands[i].getInstanceFor(line);
            }
        }
        // invalid command
        return null;
    }

    public static String getStringFor(Object object)
    {
        if(object instanceof Result)
        {
            Result res = (Result) object;
            return res.name();
        }
        else
        {
            return "";
        }
    }

    public static String[] getAllCommands()
    {
        Vector<String> res = new Vector<String>();
        for(int i = 0; i < allCommands.length; i++)
        {
            res.add(allCommands[i].getName());
        }
        return res.toArray(new String[0]);
    }

}
