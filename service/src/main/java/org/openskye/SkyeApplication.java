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
import org.openskye.config.SkyeConfiguration;
import org.openskye.exceptions.ConstraintViolationExceptionMapper;
import org.openskye.guice.*;
import org.openskye.util.RequestQueryContextFilter;
import org.openskye.util.SwaggerBundle;

import javax.annotation.Nullable;
import java.util.Properties;

/**
 * The Skye Application
 */
@Slf4j
public class SkyeApplication extends Application<SkyeConfiguration> {

    private Bootstrap<SkyeConfiguration> bootstrap;

    public static void main(String[] args) throws Exception {
        new SkyeApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<SkyeConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/apidocs", "/explore", "index.html"));
        bootstrap.addBundle(new SwaggerBundle());

        this.bootstrap = bootstrap;
    }

    @Override
    public void run(SkyeConfiguration configuration,
                    Environment environment) throws Exception {

        // Adding in the exception mappers
        environment.jersey().register(new ConstraintViolationExceptionMapper());


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

        DropwizardEnvironmentModule<SkyeConfiguration> dropwizardEnvironmentModule = new DropwizardEnvironmentModule<>(SkyeConfiguration.class);

        SkyeModule skyeModule = new SkyeModule(configuration);

        Injector injector = Guice.createInjector(jerseyContainerModule, dropwizardEnvironmentModule, jpaPersistModule, skyeModule);

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

        // Set-up the filters
        environment.servlets().addFilter("Guice Filter", GuiceFilter.class)
                .addMappingForUrlPatterns(null, false, environment.getApplicationContext().getContextPath() + "*");
        environment.servlets().addFilter("Request Query Context Filter", RequestQueryContextFilter.class)
                .addMappingForUrlPatterns(null, false, environment.getApplicationContext().getContextPath() + "*");
        environment.servlets().addFilter("Guice Persist Filter", injector.getInstance(PersistFilter.class))
                .addMappingForUrlPatterns(null, false, environment.getApplicationContext().getContextPath() + "*");

        // Complete the autoconfig
        autoConfig.run(environment, injector);

        // Add a listener for us to be able to wire in Shiro
        environment.servlets().addServletListeners(new SkyeGuiceServletContextListener(jpaPersistModule));

        // Add a listener to start the task manager
        environment.lifecycle().addServerLifecycleListener(new SkyeServerLifecycleListener(injector));
    }

}