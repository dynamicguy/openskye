package org.openskye.config;

import lombok.Data;

/**
 * Configuration for the worker.
 */
@Data
public class WorkerConfiguration {
    private int threadCount = 3;
    private int pollPeriodSec = 30;       // seconds that the worker sleeps between polls for waiting tasks
    private String hostname;
}
