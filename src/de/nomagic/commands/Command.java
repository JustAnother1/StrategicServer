package de.nomagic.commands;

import de.nomagic.server.user.User;
import de.nomagic.server.user.UserFactory;


public interface Command
{
    String getName();
    Command getInstanceFor(String line);
    Object[] getUsageDescription();
    Object[] execute(User user, UserFactory userFactory);
}
