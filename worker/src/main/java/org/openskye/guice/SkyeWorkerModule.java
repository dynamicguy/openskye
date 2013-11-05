package org.openskye.guice;

import lombok.Getter;
import lombok.Setter;
import org.openskye.config.SkyeConfiguration;
import org.openskye.config.WorkerConfiguration;
import org.openskye.task.queue.QueueTaskWorker;

import java.util.concurrent.TimeUnit;

/**
 * Guice module for the SkyeWorker application
 */
public class SkyeWorkerModule extends SkyeModule {

    @Getter
    @Setter
    WorkerConfiguration workerConfiguration = null;

    private final SkyeConfiguration skyeConfiguration = null;

    public SkyeWorkerModule(SkyeConfiguration skyeConfiguration) {
        super(skyeConfiguration);
    }

    @Override
    protected void configure() {
        super.configure();
        QueueTaskWorker.init(workerConfiguration.getName(),
                workerConfiguration.getThreadCount(),
                workerConfiguration.getPollPeriodSec(),
                TimeUnit.SECONDS);
    }
}
