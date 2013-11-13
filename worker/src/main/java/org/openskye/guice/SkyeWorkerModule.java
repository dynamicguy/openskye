package org.openskye.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import org.openskye.config.ServiceConfiguration;
import org.openskye.config.SkyeWorkerConfiguration;
import org.openskye.config.WorkerConfiguration;

/**
 * Guice module for the SkyeWorker application
 */
public class SkyeWorkerModule extends SkyeModule {

    private final SkyeWorkerConfiguration skyeWorkerConfiguration;

    public SkyeWorkerModule(SkyeWorkerConfiguration skyeWorkerConfiguration) {
        super(skyeWorkerConfiguration);
        this.skyeWorkerConfiguration = skyeWorkerConfiguration;
    }

    public void configure() {
        super.configure();
        bind(WorkerConfiguration.class).toInstance(skyeWorkerConfiguration.getWorkerConfiguration());
        bind(ServiceConfiguration.class).toInstance(skyeWorkerConfiguration.getServices());
    }
}
