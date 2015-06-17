package de.nomagic.server.matches;

import de.nomagic.commands.JoinMatchCommand;
import de.nomagic.commands.MakeMoveCommand;
import de.nomagic.commands.RequestMatchDataCommand;
import de.nomagic.server.user.User;

public interface Match
{
    Object[] getData(RequestMatchDataCommand cmd, User user);
    Object[] makeMove(MakeMoveCommand cmd, User user);
    Object[] configure(String parameter, String value);
    Object[] join(JoinMatchCommand cmd, User user);
    String getOwner();
    String getGameType();
    Match getInstanceFor(String matchName, User user);
    Object[] startGame(User user);
    boolean allStarted();
    String getActivePlayer();
    boolean isGameOngoing();
}
