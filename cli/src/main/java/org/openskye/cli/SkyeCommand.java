package org.openskye.cli;

import com.beust.jcommander.Parameter;
import org.openskye.cli.commands.ExecutableCommand;

/**
 * The core command for the Skye CLI. At the moment, it's currently a help page
 */
public class SkyeCommand extends ExecutableCommand {

    /**
     * The name of the command
     */
    private final String commandname = "help";
    /**
     * A JCommand help parameter which displays skye commands and usage
     */
    @Parameter(names = "--help", help = true)
    private boolean help;

    @Override
    public void execute() {

    }

    @Override
    public String getCommandName() {
        return commandname;
    }
}
