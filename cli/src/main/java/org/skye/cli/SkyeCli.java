package org.skye.cli;

import com.beust.jcommander.JCommander;
import lombok.extern.slf4j.Slf4j;
import org.skye.cli.commands.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The CLI for Skye
 */
@Slf4j
public class SkyeCli {

    public static void main(String[] args) throws Exception {
        new SkyeCli().run(args);
    }

    private void run(String[] args) {
        SkyeCommand skyeCommand = new SkyeCommand();
        JCommander jc = new JCommander(skyeCommand);

        // Set-up the all the commands
        List<ExecutableCommand> commands = new ArrayList<>();
        commands.add(new LoginCommand());
        commands.add(new PlatformCommand());
        commands.add(new ProjectsCommand());
        commands.add(new DomainsCommand());

        SkyeCliSettings skyeCliSettings = new SkyeCliSettings();
        skyeCliSettings.load();

        // Add all the commands to JCommander
        for (ExecutableCommand command : commands) {
            command.initialize(skyeCliSettings);
            jc.addCommand(command.getName(), command);
        }

        // Parse the command line arguments
        jc.parse(args);

        // Resolve which command we have
        for (ExecutableCommand command : commands) {
            if (command.getName().equals(jc.getParsedCommand())) {
                command.execute();
            }
        }
    }
}
