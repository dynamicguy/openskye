package org.openskye.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Just like Skye application config, but with an extra worker section
 */
public class SkyeWorkerConfiguration extends SkyeConfiguration {
    @Valid
    @NotNull
    @JsonProperty
    private WorkerConfiguration worker = new WorkerConfiguration();

    public WorkerConfiguration getWorkerConfiguration() {
        return worker;
    }
}
