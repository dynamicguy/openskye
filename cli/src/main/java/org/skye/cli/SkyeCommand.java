package org.skye.cli;

import com.beust.jcommander.Parameter;

/**
 * The core command for the Skye CLI
 */
public class SkyeCommand {

    @Parameter(names = "--help", help = true)
    private boolean help;

}
