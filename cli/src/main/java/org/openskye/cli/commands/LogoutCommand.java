package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

/**
 * The logout command
 */
@Parameters(commandDescription = "Logout of this server")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class LogoutCommand extends ExecutableCommand {

    private final String commandName = "logout";

    @Override
    public void execute() {
        settings.mustHaveApiKey();
        settings.setApiKey(null);
        settings.save();
    }
}
