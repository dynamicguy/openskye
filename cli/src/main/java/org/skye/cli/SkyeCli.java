package org.skye.cli;

import com.beust.jcommander.JCommander;
import org.skye.cli.commands.LoginCommand;

/**
 * The CLI for Skye
 */
public class SkyeCli {

    public static void main(String[] args) throws Exception {
        new SkyeCli().run(args);
    }

    private void run(String[] args) {

        SkyeCommand skyeCommand = new SkyeCommand();
        JCommander jc = new JCommander(skyeCommand);
        jc.addCommand("login", new LoginCommand());
        jc.parse(args);

        JCommander selectedCommand = jc.getCommands().get(jc.getParsedCommand());

    }
}
