package org.skye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.skye.core.SkyePlatform;

/**
 * The login command
 */
@Parameters(commandDescription = "Login to the server")
@Data
@Slf4j
public class PlatformCommand extends ExecutableCommand {

    private final String name = "platform";

    @Override
    public void execute() {
        SkyePlatform platform = getResource("platform").get(SkyePlatform.class);
        log.info("Connected to " + platform);
    }
}
