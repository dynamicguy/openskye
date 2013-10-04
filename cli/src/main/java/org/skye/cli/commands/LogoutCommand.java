package org.skye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * The login command
 */
@Parameters(commandDescription = "Login to the server")
@Data
@Slf4j
public class LogoutCommand implements ExecutableCommand {

    private final String name = "logout";

    @Override
    public void execute() {
        log.info("Logging out and removing API key");
    }
}
