package de.nomagic.commands;

import de.nomagic.server.user.User;
import de.nomagic.server.user.UserFactory;

public class SendMessageCommand extends BaseCommand implements Command
{
    public final static String INVITE_TYPE = "INVITE";
    public final static String MOVE_TYPE   = "MOVE";
    public final static String MATCH_TYPE  = "MATCH";
    public final static String CHAT_TYPE   = "CHAT";

    private String[] parts = null;
    private String[] msg = {""};
    private String ReceiverName = "";

    public SendMessageCommand()
    {
    }

    public SendMessageCommand(String line)
    {
        parts = split(line);
        if(parts.length > 1)
        {
            ReceiverName = parts[1];
        }
        if(parts.length > 2)
        {
            msg = new String[parts.length - 2];
            for(int i = 2; i < parts.length; i++)
            {
                msg[i -2] = parts[i];
            }
        }
    }

    public String getName()
    {
        return "sendMessage";
    }

    public Command getInstanceFor(String line)
    {
        return new SendMessageCommand(line);
    }

    @Override
    public Object[] getUsageDescription()
    {
        return new Object[] {Result.DATA, getName(),
                "<user name of receiver> <type> ...",
                "Type can be :",
                INVITE_TYPE + " <game type> <match Name> [<message>] - sender invites receiver to a match ",
                MOVE_TYPE + "   <game type> <match Name> [<message>] - The receiver can now make a move",
                MATCH_TYPE + "  <game type> <match Name> [<message>] - there was a change in the match configuration",
                CHAT_TYPE + "  [<game type> <match Name>] <message>  - sender wants to say something"};
    }

    @Override
    public Object[] execute(User user, UserFactory userFactory)
    {
        return userFactory.sendMessage(ReceiverName, msg, user);
    }

}
