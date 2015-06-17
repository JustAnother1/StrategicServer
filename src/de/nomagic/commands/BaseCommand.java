package de.nomagic.commands;

public abstract class BaseCommand implements Command
{
    protected String[] split(String line)
    {
        return line.split(" ");
    }

    @Override
    public Object[] getUsageDescription()
    {
        return new Object[] {Result.DATA, getName()};
    }
}
