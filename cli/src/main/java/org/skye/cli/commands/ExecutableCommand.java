package org.skye.cli.commands;

import org.skye.cli.SkyeCliSettings;

/**
 * The interface for an executable command
 */
public abstract class ExecutableCommand {

    protected SkyeCliSettings settings;

    public void initialize(SkyeCliSettings settings) {
        this.settings = settings;
    }

    public abstract void execute();

    public abstract String getName();
}
