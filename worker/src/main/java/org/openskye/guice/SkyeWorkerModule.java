package org.openskye.guice;

import com.google.inject.Inject;
import org.openskye.config.ServiceConfiguration;
import org.openskye.config.SkyeWorkerConfiguration;
import org.openskye.config.WorkerConfiguration;

/**
 * Guice module for the SkyeWorker application
 */
public class SkyeWorkerModule extends SkyeModule {

    @Inject
    public SkyeWorkerModule(SkyeWorkerConfiguration skyeWorkerConfiguration) {
        super(skyeWorkerConfiguration);
        bind(WorkerConfiguration.class).toInstance(skyeWorkerConfiguration.getWorkerConfiguration());
        bind(ServiceConfiguration.class).toInstance(skyeWorkerConfiguration.getServices());
    }
}
