package org.skye.cli;

/**
 * This is a very simple console logger that can be used
 */
public class ConsoleLogger {
    public void message(String s) {
        System.out.println(s);
    }

    public void success(String s) {
        System.out.println(s);
    }

    public void error(String s) {
        System.err.println(s);
    }

    public void raw(String s) {
        System.out.println(s);
    }
}
