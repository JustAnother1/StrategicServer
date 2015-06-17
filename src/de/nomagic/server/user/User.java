package de.nomagic.server.user;

import de.nomagic.server.Pipes.Connection;

public interface User
{
    boolean isCorrectCredential(String credentialToCheck);
    void setNewCredential(String newCredential);
    boolean isAdmin();
    void setAdmin(boolean admin);
    String getName();
    String[] getGameTypes();
    void addGameType(String gameType);
    void removeGameType(String gameType);
    boolean plays(String gameType);
    boolean isHuman();
    void setHuman(boolean human);
    boolean receiveMessage(Object[] Response);
    void addConnection(Connection con);

}
