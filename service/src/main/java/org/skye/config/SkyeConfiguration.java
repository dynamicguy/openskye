package org.skye.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * The configuration object for the Skye service
 */
public class SkyeConfiguration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty
    private DatabaseConfiguration database = new DatabaseConfiguration();
    @Valid
    @JsonProperty
    private ServiceConfiguration services = new ServiceConfiguration();

    public DatabaseConfiguration getDatabaseConfiguration() {
        return database;
    }

}
