package org.openskye.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * The configuration object for the Skye service
 */
@Data
public class SkyeConfiguration extends Configuration {

    @Valid
    @NotNull
    @JsonProperty
    private SkyeDatabaseConfiguration database = new SkyeDatabaseConfiguration();
    @Valid
    @JsonProperty
    private ServiceConfiguration services = new ServiceConfiguration();
    @Valid
    @NotNull
    @JsonProperty
    private SearchConfiguration search = new SearchConfiguration();

    public SkyeDatabaseConfiguration getDatabaseConfiguration() {
        return database;
    }

    public ServiceConfiguration getServices() {
        return services;
    }

    public SearchConfiguration getSearchConfiguration() {
        return search;
    }

    public boolean isMetricsDisabled = false;
}
