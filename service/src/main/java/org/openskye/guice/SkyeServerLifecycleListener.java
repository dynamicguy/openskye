package org.openskye.guice;

import com.google.inject.Injector;
import io.dropwizard.lifecycle.ServerLifecycleListener;
import org.eclipse.jetty.server.Server;
import org.openskye.task.TaskManager;

/**
 * A listener to perform actions that should not occur until the server is initialized
 */
public class SkyeServerLifecycleListener implements ServerLifecycleListener {

    private Injector injector;

    public SkyeServerLifecycleListener(Injector injector) {
        this.injector = injector;
    }

    @Override
    public void serverStarted(Server server) {
        TaskManager taskManager = injector.getInstance(TaskManager.class);
        taskManager.start();
    }
}
