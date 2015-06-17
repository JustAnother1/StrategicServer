package de.nomagic.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import de.nomagic.server.Pipes.Pipe;
import de.nomagic.server.Pipes.PipeFactory;
import de.nomagic.server.matches.MatchFactory;
import de.nomagic.server.user.UserFactory;

public class ServerMain extends Thread implements ServerServices
{
    private final Logger log = (Logger) LoggerFactory.getLogger(this.getClass().getName());
    private volatile boolean shouldRun = true;

    public ServerMain()
    {
    }

    public static void main(String[] args)
    {
        ServerMain m = new ServerMain();
        m.startLogging(args);
        m.getConfigFromCommandLine(args);
        m.start();
    }

    private void startLogging(final String[] args)
    {
        int numOfV = 0;
        for(int i = 0; i < args.length; i++)
        {
            if(true == "-v".equals(args[i]))
            {
                numOfV ++;
            }
        }

        // configure Logging
        switch(numOfV)
        {
        case 0: setLogLevel("warn"); break;
        case 1: setLogLevel("debug");break;
        case 2:
        default:
            setLogLevel("trace");
            System.out.println("Build from " + getCommitID());
            break;
        }
        log.info("Starting Server");
    }

    public static String getCommitID()
    {
        try
        {
            final InputStream s = ServerMain.class.getResourceAsStream("/commit-id");
            final BufferedReader in = new BufferedReader(new InputStreamReader(s));
            final String commitId = in.readLine();
            final String changes = in.readLine();
            if(null != changes)
            {
                if(0 < changes.length())
                {
                    return commitId + "-(" + changes + ")";
                }
                else
                {
                    return commitId;
                }
            }
            else
            {
                return commitId;
            }
        }
        catch( Exception e )
        {
            return e.toString();
        }
    }

    private void setLogLevel(String LogLevel)
    {
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        try
        {
            final JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            final String logCfg =
            "<configuration>" +
              "<appender name='STDOUT' class='ch.qos.logback.core.ConsoleAppender'>" +
                "<encoder>" +
                  "<pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>" +
                "</encoder>" +
              "</appender>" +
              "<root level='" + LogLevel + "'>" +
                "<appender-ref ref='STDOUT' />" +
              "</root>" +
            "</configuration>";
            ByteArrayInputStream bin;
            try
            {
                bin = new ByteArrayInputStream(logCfg.getBytes("UTF-8"));
                configurator.doConfigure(bin);
            }
            catch(UnsupportedEncodingException e)
            {
                // A system without UTF-8 ? - No chance to do anything !
                e.printStackTrace();
                System.exit(1);
            }
        }
        catch (JoranException je)
        {
          // StatusPrinter will handle this
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }


    public void run()
    {
        // Startup
        shouldRun = true;
        MatchFactory mf = new MatchFactory();
        UserFactory uf = new UserFactory();
        uf.addServerServices(this);
        uf.addMatchFactory(mf);
        PipeFactory pf = new PipeFactory();
        Vector<Pipe> pipes = new Vector<Pipe>();
        Pipe cli = pf.getPipe("console", uf);
        if(null != cli)
        {
            pipes.addElement(cli);
            cli.start();
        }
        Pipe tcp = pf.getPipe("tcp:4223", uf);
        if(null != tcp)
        {
            pipes.addElement(tcp);
            tcp.start();
        }
        // Server Running
        while(true == shouldRun)
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                // ok
            }
        }
        // Shutdown
        for(int i = 0; i < pipes.size(); i++)
        {
            Pipe p = pipes.get(i);
            p.shutDown();
        }
        pf.shutDown();
        mf.shutDown();
        uf.shutDown();
        System.out.println("Done!");
        System.exit(0);
    }

    public void getConfigFromCommandLine(String[] args)
    {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean shutDownServer()
    {
        shouldRun = false;
        this.interrupt();
        return true;
    }

}
