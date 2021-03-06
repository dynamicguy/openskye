package org.openskye.cli;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.MissingCommandException;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.*;
import org.openskye.core.SkyeException;
import org.openskye.exceptions.ExceptionMessage;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * The Skye command line interface
 */
@Slf4j
public class SkyeCli {

    /**
     * The Skye CLI main class. The logger is initialized, and the command sent to the CLI is run.
     *
     * @param args the command entered into the CLI
     *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.OFF);

        new SkyeCli().run(args);
    }

    /**
     * Parses and runs the given command.
     *
     * @param args the command entered into the CLI
     */
    private void run(String[] args) {

        // Lets turn off logging - we can turn it back on if they have
        // debug set

        SkyeCommand skyeCommand = new SkyeCommand();
        JCommander jc = new JCommander(skyeCommand);
        jc.setProgramName("openskye");


        ConsoleLogger consoleLogger = new ConsoleLogger();
        // Set-up the all the commands
        List<ExecutableCommand> commands = new ArrayList<>();
        commands.add(new LoginCommand());
        commands.add(new PlatformCommand());
        commands.add(new ProjectsCommand());
        commands.add(new DomainsCommand());
        commands.add(new UsersCommand());
        commands.add(new RolesCommand());
        commands.add(new UserRolesCommand());
        commands.add(new ArchiveStoreDefinitionsCommand());
        commands.add(new ArchiveStoreInstancesCommand());
        commands.add(new InformationStoreDefinitionsCommand());
        commands.add(new MetadataTemplatesCommand());
        commands.add(new NodesCommand());
        commands.add(new TasksCommand());
        commands.add(new TaskLogsCommand());
        commands.add(new TaskSchedulesCommand());
        commands.add(new ChannelsCommand());
        commands.add(new ObjectSetsCommand());
        commands.add(new ObjectsCommand());
        commands.add(new ProjectUsersCommand());
        commands.add(new RetentionPoliciesCommand());
        commands.add(new HoldsCommand());
        commands.add(new LogoutCommand());
        commands.add(new SkyeCommand());

        SkyeCliSettings skyeCliSettings = SkyeCliSettings.load();

        // Add all the commands to JCommander
        for (ExecutableCommand command : commands) {
            command.initialize(skyeCliSettings, consoleLogger);
            jc.addCommand(command.getCommandName(), command);
        }

        // Parse the command line arguments
        try {
            jc.parse(args);

            // If no command, then do nothing
            if (jc.getParsedCommand() == null) {
                return;
            }

            // Resolve which command we have
            for (ExecutableCommand command : commands) {
                if (command.getCommandName() == null) {
                    consoleLogger.error("Missing command name for " + command.getClass().getSimpleName());
                } else if (command.getCommandName().equals(jc.getParsedCommand())) {
                    if (command.getCommandName().equals("help")) {
                        jc.usage();
                    } else {
                        command.execute();
                    }
                }
            }
        } catch (CliExitCodeException e) {
            consoleLogger.message(e.getMessage());
            System.exit(e.getExitCode());
        } catch (MissingCommandException e) {
            jc.usage();
        } catch (ClientHandlerException e) {
            consoleLogger.error("Unable to connect to server: ");
            printException(consoleLogger,e);
        } catch (CliException c) {
            consoleLogger.error(c.getLocalizedMessage());
            printException(consoleLogger,c);
        } catch (SkyeException se) {
            consoleLogger.error(se.getLocalizedMessage());
            printException(consoleLogger,se);
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == 401) {
                consoleLogger.error("Not authorized, has your API key changed?");
            } else {
                consoleLogger.error(e.getLocalizedMessage());
                try {
                    ExceptionMessage message = e.getResponse().getEntity(ExceptionMessage.class);
                    if (message.getErrorCode() > 0) {
                        consoleLogger.error("  error code: " + message.getErrorCode());
                        consoleLogger.error("  message: " + message.getMessage());
                        consoleLogger.error("  detail: " + message.getDetail());
                    }
                } catch (Exception ee) {
                    // Don't even try to handle exceptions within catch block .. ignore
                }
            }
        }

    }

    private void printException( ConsoleLogger consoleLogger, Throwable e ) {
        if ( e != null ) {
            consoleLogger.error(e.getClass().getName()+": "+e.getMessage());
            e.printStackTrace();
            printException(consoleLogger,e.getCause());
        }
    }
}
