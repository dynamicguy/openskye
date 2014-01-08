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
import javax.ws.rs.ext.ExceptionMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * The Skye Application
 * <p/>
 * This is the main java file run whenever Skye starts. It reads information from the yml provided by the user when
 * calling the software
 */
@Slf4j
public class SkyeApplication extends Application<SkyeConfiguration> {

    /**
     * The pre-start application environment, used to start Dropwizard
     *
     * @see io.dropwizard.setup.Bootstrap
     */
    private Bootstrap<SkyeConfiguration> bootstrap;

    /**
     * The main method for Skye. Parameters are taken from the command line in the form of:
     * <p/>
     * skye server skye.<!-- -->yml
     * <p/>
     * This file contains relevant configuration information for Skye (database configuration, url location)
     *
     * @param args - The yml file passed from the command line call
     *
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        new SkyeApplication().run(args);
    }

    /**
     * Initializes the Skye application by adding the api documents to the bootstrap, as well as a newly initiallized
     * swagger bundle. The Swagger API for Skye is created here (implementation of io.dropwizard.Application
     * initialize(Bootstrap<T> bootstrap))
     *
     * @param bootstrap - The Dropwizard application environment
     *
     * @see io.dropwizard.Application
     */
    @Override
    public void initialize(Bootstrap<SkyeConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/apidocs", "/explore", "index.html"));
        bootstrap.addBundle(new SwaggerBundle());

        this.bootstrap = bootstrap;
    }

    /**
     * Runs the setup required for Skye by replacing Dropwizard ExceptionMappers with Skye ExceptionMappers, sets up the
     * JPA module, wire in Shiro, and perform Guice injections
     *
     * @param configuration - The configuration file (yml) passed when calling skye server
     * @param environment   - The environment for the Skye application
     *
     * @throws Exception
     */
    @Override
    public void run(SkyeConfiguration configuration,
                    Environment environment) throws Exception {

        // Remove all of Dropwizard's custom ExceptionMappers
        ResourceConfig jrConfig = environment.jersey().getResourceConfig();
        Set<Object> dwSingletons = jrConfig.getSingletons();
        List<Object> singletonsToRemove = new ArrayList<Object>();

        for (Object s : dwSingletons) {
            if (s instanceof ExceptionMapper && s.getClass().getName().startsWith("io.dropwizard.jersey.")) {
                singletonsToRemove.add(s);
            }
        }

        for (Object s : singletonsToRemove) {
            jrConfig.getSingletons().remove(s);
        }

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
        props.put("hibernate.dialect", configuration.getDatabaseConfiguration().getDialect());
        props.put("hibernate.connection.provider_class", configuration.getDatabaseConfiguration().getConnectionProviderClass());
        props.put("hibernate.c3p0.max_size", configuration.getDatabaseConfiguration().getPoolMaxSize());
        props.put("hibernate.c3p0.min_size", configuration.getDatabaseConfiguration().getPoolMinSize());
        props.put("hibernate.c3p0.timeout", configuration.getDatabaseConfiguration().getPoolTimeOut());
        props.put("hibernate.c3p0.idle_test_period", configuration.getDatabaseConfiguration().getPoolIdleTestPeriod());
        props.put("hibernate.c3p0.preferredTestQuery", configuration.getDatabaseConfiguration().getPoolPreferredTestQuery());
        props.put("hibernate.c3p0.testConnectionOnCheckout", configuration.getDatabaseConfiguration().getPoolTestConnectionOnCheckout());
        props.put("hibernate.c3p0.poolConnectionMaxIdleTime", configuration.getDatabaseConfiguration().getPoolConnectionMaxIdleTime());
        props.put("hibernate.c3p0.maxIdleTimeExcessConnections", configuration.getDatabaseConfiguration().getMaxIdleTimeExcessConnections());
        props.put("hibernate.c3p0.unreturnedConnectionTimeout", configuration.getDatabaseConfiguration().getUnreturnedConnectionTimeout());
        props.put("hibernate.c3p0.debugUnreturnedConnectionStackTraces", "true");
        
        jpaPersistModule.properties(props);

        DropwizardEnvironmentModule<SkyeConfiguration> dropwizardEnvironmentModule = new DropwizardEnvironmentModule<>(SkyeConfiguration.class);

        SkyeModule skyeModule = new SkyeModule(configuration);

        Injector injector = Guice.createInjector(jerseyContainerModule, dropwizardEnvironmentModule, jpaPersistModule, skyeModule);

        // Set-up the filters
        environment.servlets().addFilter("Guice Persist Filter", injector.getInstance(PersistFilter.class))
                .addMappingForUrlPatterns(null, false, environment.getApplicationContext().getContextPath() + "*");
        environment.servlets().addFilter("Guice Filter", GuiceFilter.class)
                .addMappingForUrlPatterns(null, false, environment.getApplicationContext().getContextPath() + "*");

        environment.servlets().addFilter("Request Query Context Filter", RequestQueryContextFilter.class)
                .addMappingForUrlPatterns(null, false, environment.getApplicationContext().getContextPath() + "*");

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




        // Complete the autoconfig
        autoConfig.run(environment, injector);

        // Add a listener for us to be able to wire in Shiro
        environment.servlets().addServletListeners(new SkyeGuiceServletContextListener(jpaPersistModule));

        // Add a listener to start the task manager
        environment.lifecycle().addServerLifecycleListener(new SkyeServerLifecycleListener(injector));
    }

}