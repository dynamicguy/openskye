package org.openskye.config;

import io.dropwizard.Configuration;
import lombok.Data;

/**
 * Configuration for the worker.
 */
@Data
public class WorkerConfiguration extends Configuration {
    private int threadCount = 3;
    private int pollPeriodSec = 30;       // seconds that the worker sleeps between polls for waiting tasks
    private String hostname;
}
