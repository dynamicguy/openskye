package org.skye;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceFilter;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import lombok.extern.slf4j.Slf4j;
import org.skye.config.SkyeConfiguration;
import org.skye.guice.AutoConfig;
import org.skye.guice.GuiceContainer;
import org.skye.guice.JerseyContainerModule;
import org.skye.guice.SkyeGuiceServletContextListener;
import org.skye.util.SwaggerBundle;

import java.util.Properties;

/**
 * The Skye Service
 */
@Slf4j
public class SkyeService extends Service<SkyeConfiguration> {

    public static void main(String[] args) throws Exception {
        new SkyeService().run(args);
    }

    @Override
    public void initialize(Bootstrap<SkyeConfiguration> bootstrap) {

        bootstrap.setName("skye");
        bootstrap.addBundle(new AssetsBundle("/apidocs", "/explore", "index.html"));
        bootstrap.addBundle(new SwaggerBundle());

    }

    @Override
    public void run(SkyeConfiguration configuration,
                    Environment environment) throws Exception {


        // Set-up the container
        GuiceContainer container = new GuiceContainer();

        // Set-up the persistence
        JpaPersistModule jpaPersistModule = new JpaPersistModule("Default");
        Properties props = new Properties();
        props.put("javax.persistence.jdbc.url", configuration.getDatabaseConfiguration().getUrl());
        props.put("javax.persistence.jdbc.user", configuration.getDatabaseConfiguration().getUser());
        props.put("javax.persistence.jdbc.password", configuration.getDatabaseConfiguration().getPassword());
        props.put("javax.persistence.jdbc.driver", configuration.getDatabaseConfiguration().getDriverClass());
        jpaPersistModule.properties(props);

        // Set-up Jersey container
        JerseyContainerModule jerseyContainerModule = new JerseyContainerModule(container);

        // Create injector
        Injector injector = Guice.createInjector(jpaPersistModule, jerseyContainerModule);

        // Auto config all services based on reflections
        new AutoConfig(SkyeService.class.getPackage().getName()).run(environment, injector);

        // Wire it together
        container.setResourceConfig(environment.getJerseyResourceConfig());
        environment.setJerseyServletContainer(container);

        // Hook in the filters
        environment.addFilter(new GuiceFilter(), configuration.getHttpConfiguration().getRootPath());
        environment.addFilter(injector.getInstance(PersistFilter.class), configuration.getHttpConfiguration().getRootPath());
        environment.addServletListeners(new SkyeGuiceServletContextListener(jpaPersistModule));
    }

}