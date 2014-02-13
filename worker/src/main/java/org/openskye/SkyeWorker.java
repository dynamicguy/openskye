package org.openskye;

import com.google.common.base.Function;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.openskye.config.SkyeWorkerConfiguration;
import org.openskye.exceptions.ConstraintViolationExceptionMapper;
import org.openskye.guice.*;
import org.openskye.util.RequestQueryContextFilter;

import javax.annotation.Nullable;
import javax.ws.rs.ext.ExceptionMapper;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * The main java file run in order to start a Skye Worker instance
 */
@Slf4j
public class SkyeWorker extends Application<SkyeWorkerConfiguration> {

    private static String argsPath;

    /**
     * The pre-start application environment, used to start Dropwizard
     *
     * @see io.dropwizard.setup.Bootstrap
     */
    private Bootstrap<SkyeWorkerConfiguration> bootstrap;

    /**
     * The main method for the skye worker. Parameters are taken from the command line in the form of:
     * <p/>
     * skye server skye-worker.<!-- -->yml
     * <p/>
     * This file contains relevant configuration information for the skye worker (database configuration, url location)
     *
     * @param args - The yml file passed from the command line call
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        argsPath = args[args.length-1];
        new SkyeWorker().run(args);
    }

    /**
     * Initializes the Skye application by adding the api documents to the bootstrap, as well as a newly initiallized
     * swagger bundle. The Swagger API for Skye is created here (implementation of io.dropwizard.Application
     * initialize(Bootstrap<T> bootstrap))
     *
     * @param bootstrap - The Dropwizard application environment
     * @see io.dropwizard.Application
     */
    @Override
    public void initialize(Bootstrap<SkyeWorkerConfiguration> bootstrap) {
        this.bootstrap = bootstrap;
    }

    /**
     * Runs the setup required for Skye by replacing Dropwizard ExceptionMappers with Skye ExceptionMappers, sets up the
     * JPA module, wire in Shiro, start the task manager, and perform Guice injections
     *
     * @param configuration - The configuration file (yml) passed when calling skye server
     * @param environment   - The environment for the Skye application
     * @throws Exception
     */
    public void run(SkyeWorkerConfiguration configuration,
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

        Path path = Paths.get(argsPath);
        Charset charset = StandardCharsets.UTF_8;

        String content = new String(Files.readAllBytes(path), charset);
        String passwordString = "password: ";
        String contentPortion = content.substring(content.lastIndexOf(passwordString)+passwordString.length(), content.length());
        String oldPassword = contentPortion.substring(0, contentPortion.indexOf(System.getProperty("line.separator")));
        if(oldPassword.equals(configuration.getDatabaseConfiguration().getPassword())){
            String newPassword = configuration.getDatabaseConfiguration().hashPw(oldPassword);
            configuration.getDatabaseConfiguration().setPassword(newPassword);
            String newContent = contentPortion.replace(configuration.getDatabaseConfiguration().getPassword(), configuration.getDatabaseConfiguration().hashPw(configuration.getDatabaseConfiguration().getPassword()));
            content = content.replace(contentPortion, newContent);
            Files.write(path, content.getBytes(charset));
        }

        DropwizardEnvironmentModule<SkyeWorkerConfiguration> dropwizardEnvironmentModule = new DropwizardEnvironmentModule<>(SkyeWorkerConfiguration.class);
        dropwizardEnvironmentModule.setEnvironmentData(configuration, environment);

        SkyeModule skyeModule = new SkyeModule(configuration);

        // Disable the detailed metrics if requested
        if (configuration.isMetricsDisabled()) {
            bootstrap.getMetricRegistry().addListener(new MetricDisablerListener(bootstrap));
        }

        // Set-up the filters
        environment.servlets().addFilter("Request Query Context Filter", RequestQueryContextFilter.class)
                .addMappingForUrlPatterns(null, false, environment.getApplicationContext().getContextPath() + "*");

        container.setResourceConfig(environment.jersey().getResourceConfig());
        environment.jersey().replace(new Function<ResourceConfig, ServletContainer>() {
            @Nullable
            @Override
            public ServletContainer apply(ResourceConfig resourceConfig) {
                return container;
            }
        });

        // Add a listener for us to be able to wire in Shiro and then bootstrap all the modules off this
        // so we only have one guice injector
        environment.servlets().addServletListeners(new SkyeGuiceServletContextListener(jpaPersistModule, environment, bootstrap, jerseyContainerModule, dropwizardEnvironmentModule, skyeModule));
    }

}