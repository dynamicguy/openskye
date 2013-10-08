package org.skye;

import com.google.inject.persist.jpa.JpaPersistModule;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.skye.config.SkyeConfiguration;
import org.skye.guice.GuiceBundle;
import org.skye.util.SwaggerBundle;

/**
 * The Skye Service
 */
@Slf4j
public class SkyeApplication extends Application<SkyeConfiguration> {

    public static void main(String[] args) throws Exception {
        new SkyeApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<SkyeConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/apidocs", "/explore", "index.html"));
        bootstrap.addBundle(new SwaggerBundle());

        // Set-up Guice
        // Set-up the persistence
        JpaPersistModule jpaPersistModule = new JpaPersistModule("Default");
//        Properties props = new Properties();
//        props.put("javax.persistence.jdbc.url", configuration.getDatabaseConfiguration().getUrl());
//        props.put("javax.persistence.jdbc.user", configuration.getDatabaseConfiguration().getUser());
//        props.put("javax.persistence.jdbc.password", configuration.getDatabaseConfiguration().getPassword());
//        props.put("javax.persistence.jdbc.driver", configuration.getDatabaseConfiguration().getDriverClass());
//        jpaPersistModule.properties(props);

        bootstrap.addBundle(GuiceBundle.newBuilder()
                .addModule(jpaPersistModule)
                .enableAutoConfig(getClass().getPackage().getName())
                .build()
        );

    }

    @Override
    public void run(SkyeConfiguration configuration,
                    Environment environment) throws Exception {


    }

}