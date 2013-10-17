package org.openskye.cli;

/**
 * A simple representation of an exception in the CLI
 * <p/>
 * This type of exception is just displayed and can't be handled
 */
public class CliException extends RuntimeException {

    public CliException(String s) {
        super(s);
    }
}
