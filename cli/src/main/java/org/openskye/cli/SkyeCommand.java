package org.openskye.cli;

import com.beust.jcommander.Parameter;
import org.openskye.cli.commands.ExecutableCommand;

/**
 * The core command for the Skye CLI
 */
public class SkyeCommand extends ExecutableCommand{

    @Parameter(names = "--help", help = true)
    private boolean help;
    private final String commandname = "help";

    @Override
    public void execute() {

    }

    @Override
    public String getCommandName() {
        return commandname;
    }
}
