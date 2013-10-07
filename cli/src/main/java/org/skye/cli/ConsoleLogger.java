package org.skye.cli;

import org.apache.commons.lang.StringUtils;
import org.fusesource.jansi.AnsiConsole;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * This is a very simple console logger that can be used
 */
public class ConsoleLogger {

    public void bold(String s) {
        AnsiConsole.out.println(ansi().bold().fg(YELLOW).a(s).reset());
    }

    public void message(String s) {
        AnsiConsole.out.println(s);
    }

    public void success(String s) {
        AnsiConsole.out.println(ansi().fg(GREEN).a(s).reset());
    }

    public void error(String s) {
        AnsiConsole.out.println(ansi().fg(RED).a(s).reset());
    }

    public void raw(String s) {
        AnsiConsole.out.println(s);
    }

    public void insertLines(int i) {
        AnsiConsole.out.println(StringUtils.repeat("\n", i));
    }
}
