package org.skye.cli.commands;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.skye.cli.SkyeCliSettings;

import javax.ws.rs.core.MediaType;

/**
 * The interface for an executable command
 */
public abstract class ExecutableCommand {

    protected SkyeCliSettings settings;
    protected Client client = Client.create();

    public void initialize(SkyeCliSettings settings) {
        this.settings = settings;
    }

    protected WebResource.Builder getResource(String path) {
        return client
                .resource(settings.getUrl() + path).queryParam("apikey", settings.getApiKey()).type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE);
    }

    public abstract void execute();

    public abstract String getCommandName();
}
