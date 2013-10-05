package org.skye.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.skye.resource.UserSelf;

/**
 * The login command
 */
@Parameters(commandDescription = "Login to the server")
@Data
@Slf4j
public class LoginCommand extends ExecutableCommand {

    private final String commandName = "login";
    @Parameter(names = "--username")
    private String user;
    @Parameter(names = "--password")
    private String password;
    @Parameter(names = "--url")
    private String url = "http://localhost:5000/api/1";

    @Override
    public void execute() {

        log.info("Logging in as " + user + " to " + url);
        settings.setUrl(url);
        UserSelf userSelf = getResource("account").get(UserSelf.class);
        System.out.println(userSelf);
    }


}
