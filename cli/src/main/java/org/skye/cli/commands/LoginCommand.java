package org.skye.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * The login command
 */
@Parameters(commandDescription = "Login to the server")
public class LoginCommand implements ExecutableCommand {

    @Parameter(names = "--user")
    private String user;
    @Parameter(names = "--password")
    private String password;
    @Parameter(names = "--url")
    private String url;

    @Override
    public void execute() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
