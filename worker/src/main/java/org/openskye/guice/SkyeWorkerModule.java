package org.openskye.guice;

import lombok.Getter;
import lombok.Setter;
import org.openskye.config.ServiceConfiguration;
import org.openskye.config.SkyeWorkerConfiguration;
import org.openskye.config.WorkerConfiguration;

/**
 * Guice module for the SkyeWorker application
 */
public class SkyeWorkerModule extends SkyeModule {

    @Getter
    @Setter
    WorkerConfiguration workerConfiguration = null;
    @Getter
    @Setter
    ServiceConfiguration serviceConfiguration = null;

    public SkyeWorkerModule(SkyeWorkerConfiguration skyeWorkerConfiguration) {
        super(skyeWorkerConfiguration);
    }
}
