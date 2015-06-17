package de.nomagic.server.Pipes;

import de.nomagic.server.user.UserFactory;

public interface Connection
{
    void close();
    void addUserFactory(UserFactory userFactory);
    boolean sendResponse(Object[] response);
    boolean isOpen();
}
