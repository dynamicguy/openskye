package org.openskye.cli;

import org.apache.commons.lang.StringUtils;
import org.fusesource.jansi.AnsiConsole;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * This is a very simple console logger that can be used to output various message types.
 */
public class ConsoleLogger {
    /**
     * Output a bold message to the console
     *
     * @param s the message to output
     */
    public void bold(String s) {
        AnsiConsole.out.println(ansi().bold().fg(YELLOW).a(s).reset());
    }

    /**
     * Output a standard message to the console
     *
     * @param s the message to output
     */
    public void message(String s) {
        AnsiConsole.out.println(s);
    }

    /**
     * Output a success message to the console
     *
     * @param s the message to output
     */
    public void success(String s) {
        AnsiConsole.out.println(ansi().fg(GREEN).a(s).reset());
    }

    /**
     * Output an error message to the console
     *
     * @param s the message to output
     */
    public void error(String s) {
        AnsiConsole.out.println(ansi().fg(RED).a(s).reset());
    }

    /**
     * Output raw text to the console
     *
     * @param s the text to output
     */
    public void raw(String s) {
        AnsiConsole.out.println(s);
    }

    /**
     * Insert a number of blank lines into the console output
     *
     * @param i the number of lines to insert
     */
    public void insertLines(int i) {
        AnsiConsole.out.println(StringUtils.repeat("\n", i));
    }
}
