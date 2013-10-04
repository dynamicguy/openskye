package org.skye.cli;

import com.beust.jcommander.JCommander;
import lombok.extern.slf4j.Slf4j;
import org.skye.cli.commands.ExecutableCommand;
import org.skye.cli.commands.LoginCommand;

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
        log.info("Loading settings...");
        SkyeCommand skyeCommand = new SkyeCommand();
        JCommander jc = new JCommander(skyeCommand);

        // Set-up the all the commands
        List<ExecutableCommand> commands = new ArrayList<>();
        commands.add(new LoginCommand());

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
