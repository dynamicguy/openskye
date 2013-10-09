package org.skye.cli.commands;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.skye.cli.ConsoleLogger;
import org.skye.cli.SkyeCliSettings;

import javax.ws.rs.core.MediaType;
import java.io.Console;

/**
 * The interface for an executable command
 */
public abstract class ExecutableCommand {

    protected SkyeCliSettings settings;
    protected Client client = Client.create();
    protected ConsoleLogger output;

    public void initialize(SkyeCliSettings settings, ConsoleLogger consoleLogger) {
        this.settings = settings;
        this.output = consoleLogger;
    }

    protected WebResource.Builder getResource(String path) {
        WebResource webResource = client
                .resource(settings.getUrl() + path);
        WebResource.Builder result = webResource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE);

        // If we have an API key in place then we will use it

        if (settings.getApiKey() != null) {
            result.header("X-Api-Key", settings.getApiKey());
        }
        return result;
    }

    public abstract void execute();

    public abstract String getCommandName();

    public Console getConsole() {
        return System.console();
    }
}
