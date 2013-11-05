package org.openskye.config;

import io.dropwizard.Configuration;
import lombok.Data;

/**
 * Configuration for the worker.
 */
@Data
public class WorkerConfiguration extends Configuration {
    private int threadCount;
    private int cancelTimeoutSec;  // seconds that the worker waits when trying to cancel tasks
    private int pollPeriodSec;     // seconds that the worker sleeps between polls for waiting tasks
    private String name;     // a name by which this worker instance should be known
}
