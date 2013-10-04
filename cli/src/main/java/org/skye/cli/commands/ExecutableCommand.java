package org.skye.cli.commands;

/**
 * The interface for an executable command
 */
public interface ExecutableCommand {

    void execute();

    String getName();
}
