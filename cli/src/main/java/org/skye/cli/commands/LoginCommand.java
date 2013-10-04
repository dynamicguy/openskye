package org.skye.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * The login command
 */
@Parameters(commandDescription = "Login to the server")
@Data
@Slf4j
public class LoginCommand extends ExecutableCommand {

    private final String name = "login";
    @Parameter(names = "--username")
    private String user;
    @Parameter(names = "--password")
    private String password;
    @Parameter(names = "--url")
    private String url = "http://localhost:5000/api/1";

    @Override
    public void execute() {
        log.info("Starting login as " + user + " to " + url);
    }
}
