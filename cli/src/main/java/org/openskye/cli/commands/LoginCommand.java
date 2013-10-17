package org.openskye.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.openskye.resource.UserSelf;

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
    private String url = "http://localhost:5000/api/1/";

    @Override
    public void execute() {

        output.message("Logging in as " + user + " at " + url);
        settings.setUrl(url);

        // Since we are trying to test the username and password
        // lets ignore the api key and use basic auth on the
        // /account path
        client.addFilter(new HTTPBasicAuthFilter(user, password));
        settings.setApiKey(null);
        try {
            UserSelf userSelf = getResource("account").get(UserSelf.class);
            output.success("Login successful, storing credentials");
            settings.setApiKey(userSelf.getApiKey());
            settings.save();
        } catch (UniformInterfaceException e) {
            if (e.getResponse().getStatus() == 401) {
                output.error("Your username or password is incorrect");
            } else {
                output.error(e.getLocalizedMessage());
            }
        }

    }


}