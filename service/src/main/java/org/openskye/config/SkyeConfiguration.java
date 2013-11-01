package org.openskye.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

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

    public ServiceConfiguration getServices() {
        return services;
    }

}
