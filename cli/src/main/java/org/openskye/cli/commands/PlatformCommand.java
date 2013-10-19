package org.openskye.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.core.SkyePlatform;
import org.openskye.stores.StoreRegistryMetadata;

/**
 * Get the platform details
 */
@Parameters(commandDescription = "Get the platform details for the Skye instance")
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
