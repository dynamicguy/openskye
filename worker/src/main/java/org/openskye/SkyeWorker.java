package org.openskye;

import com.google.common.base.Function;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceFilter;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.util.Attributes;
import org.openskye.config.SkyeConfiguration;
import org.openskye.config.SkyeWorkerConfiguration;
import org.openskye.guice.*;
import org.openskye.task.TaskManager;
import org.openskye.task.queue.QueueWorkerManager;
import org.openskye.util.SwaggerBundle;

import javax.annotation.Nullable;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

/**
 * The Skye Application
 */
@Slf4j
public class SkyeWorker extends SkyeApplication {

    private Bootstrap<SkyeWorkerConfiguration> bootstrap;

    public static void main(String[] args) throws Exception {
        new SkyeWorker().run(args);
    }

    @Override
    public void run(SkyeConfiguration configuration,
                    Environment environment) throws Exception {

        SkyeWorkerConfiguration skyeWorkerConfiguration = (SkyeWorkerConfiguration) configuration;

        final GuiceContainer container = new GuiceContainer();
        JerseyContainerModule jerseyContainerModule = new JerseyContainerModule(container);

        // Set-up the persistence
        JpaPersistModule jpaPersistModule = new JpaPersistModule("Default");
        Properties props = new Properties();
        props.put("javax.persistence.jdbc.url", configuration.getDatabaseConfiguration().getUrl());
        props.put("javax.persistence.jdbc.user", configuration.getDatabaseConfiguration().getUser());
        props.put("javax.persistence.jdbc.password", configuration.getDatabaseConfiguration().getPassword());
        props.put("javax.persistence.jdbc.driver", configuration.getDatabaseConfiguration().getDriverClass());
        jpaPersistModule.properties(props);

        DropwizardEnvironmentModule<SkyeWorkerConfiguration> dropwizardEnvironmentModule = new DropwizardEnvironmentModule<>(SkyeWorkerConfiguration.class);

        SkyeWorkerModule skyeWorkerModule = new SkyeWorkerModule(skyeWorkerConfiguration);
        skyeWorkerModule.setServiceConfiguration(skyeWorkerConfiguration.getServices());
        skyeWorkerModule.setWorkerConfiguration(skyeWorkerConfiguration.getWorkerConfiguration());

        Injector injector = Guice.createInjector(jerseyContainerModule, dropwizardEnvironmentModule, jpaPersistModule, skyeWorkerModule);

        AutoConfig autoConfig = new AutoConfig(this.getClass().getPackage().getName());
        autoConfig.initialize(bootstrap, injector);

        container.setResourceConfig(environment.jersey().getResourceConfig());
        environment.jersey().replace(new Function<ResourceConfig, ServletContainer>() {
            @Nullable
            @Override
            public ServletContainer apply(ResourceConfig resourceConfig) {
                return container;
            }
        });
        environment.servlets().addFilter("Guice Filter", GuiceFilter.class)
                .addMappingForUrlPatterns(null, false, environment.getApplicationContext().getContextPath() + "*");

        // Complete the autoconfig
        autoConfig.run(environment, injector);

        // Configure and run the worker manager
        QueueWorkerManager queueWorkerManager = (QueueWorkerManager) injector.getInstance(TaskManager.class);
        queueWorkerManager.setWorkerConfiguration(skyeWorkerConfiguration.getWorkerConfiguration());
        queueWorkerManager.start();

        environment.servlets().addFilter("Guice Persist Filter", injector.getInstance(PersistFilter.class))
                .addMappingForUrlPatterns(null, false, environment.getApplicationContext().getContextPath() + "*");

    }

}