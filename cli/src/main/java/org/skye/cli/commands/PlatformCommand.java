package org.skye.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.skye.core.SkyePlatform;
import org.skye.stores.StoreRegistryMetadata;

/**
 * The login command
 */
@Parameters(commandDescription = "Login to the server")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class PlatformCommand extends ExecutableCommand {

    private final String commandName = "platform";
    @Parameter(names = "--registry")
    private boolean registry;

    @Override
    public void execute() {
        SkyePlatform platform = getResource("platform").get(SkyePlatform.class);
        log.info("Connected to " + platform);
        if (registry) {
            StoreRegistryMetadata registryMetadata = getResource("platform/registry").get(StoreRegistryMetadata.class);
            log.info(registryMetadata.toString());
        }

    }
}
