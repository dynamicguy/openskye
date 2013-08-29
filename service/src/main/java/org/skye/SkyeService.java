package org.skye;

import com.google.inject.persist.jpa.JpaPersistModule;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import lombok.extern.slf4j.Slf4j;
import org.skye.config.SkyeConfiguration;
import org.skye.guice.GuiceBundle;
import org.skye.security.SkyeGuiceServletContextListener;
import org.skye.util.SwaggerBundle;

import java.util.Properties;

/**
 * The Skye Service
 */
@Slf4j
public class SkyeService extends Service<SkyeConfiguration> {

    private GuiceBundle guiceBundle;
    private JpaPersistModule jpaPersistModule;

    public static void main(String[] args) throws Exception {
        new SkyeService().run(args);
    }

    @Override
    public void initialize(Bootstrap<SkyeConfiguration> bootstrap) {

        // Create a JPA persistence bundle
        this.jpaPersistModule = new JpaPersistModule("Default");

        bootstrap.setName("skye");
        this.guiceBundle = buildGuiceBundle();
        bootstrap.addBundle(guiceBundle);
        bootstrap.addBundle(new AssetsBundle("/apidocs", "/explore", "index.html"));
        bootstrap.addBundle(new SwaggerBundle());

    }

    public GuiceBundle buildGuiceBundle() {
        return GuiceBundle.<SkyeConfiguration>newBuilder()
                .addModule(new SkyeTestModule())
                .addModule(jpaPersistModule)
                .enableAutoConfig(getClass().getPackage().getName())
                .setConfigClass(SkyeConfiguration.class)
                .build();
    }

    @Override
    public void run(SkyeConfiguration configuration,
                    Environment environment) throws Exception {

        Properties props = new Properties();
        props.put("javax.persistence.jdbc.url", configuration.getDatabaseConfiguration().getUrl());
        props.put("javax.persistence.jdbc.user", configuration.getDatabaseConfiguration().getUser());
        props.put("javax.persistence.jdbc.password", configuration.getDatabaseConfiguration().getPassword());
        props.put("javax.persistence.jdbc.driver", configuration.getDatabaseConfiguration().getDriverClass());
        jpaPersistModule.properties(props);

        environment.addServletListeners(new SkyeGuiceServletContextListener(jpaPersistModule));
    }

}