package org.skye;

import com.hubspot.dropwizard.guice.GuiceBundle;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.hibernate.HibernateBundle;
import org.skye.config.SkyeConfiguration;
import org.skye.domain.*;
import org.skye.util.SwaggerBundle;

/**
 * The Skye Service
 */
public class SkyeService extends Service<SkyeConfiguration> {

    private final HibernateBundle<SkyeConfiguration> hibernate = new HibernateBundle<SkyeConfiguration>(Domain.class, Channel.class, Project.class, User.class, UserRole.class, Role.class, Permission.class, ChannelArchiveStore.class, DomainArchiveStore.class, ArchiveStoreInstance.class) {
        @Override
        public DatabaseConfiguration getDatabaseConfiguration(SkyeConfiguration configuration) {
            return configuration.getDatabaseConfiguration();
        }
    };

    public static void main(String[] args) throws Exception {
        new SkyeService().run(args);
    }

    @Override
    public void initialize(Bootstrap<SkyeConfiguration> bootstrap) {

        bootstrap.setName("skye");

        bootstrap.addBundle(hibernate);
        GuiceBundle<SkyeConfiguration> guiceBundle = GuiceBundle.<SkyeConfiguration>newBuilder()
                .addModule(new SkyeModule(hibernate))
                .enableAutoConfig(getClass().getPackage().getName())
                .setConfigClass(SkyeConfiguration.class)
                .build();

        bootstrap.addBundle(guiceBundle);
        bootstrap.addBundle(new SwaggerBundle());
        bootstrap.addBundle(new AssetsBundle("/apidocs", "/explore", "index.html"));
    }

    @Override
    public void run(SkyeConfiguration configuration,
                    Environment environment) {
    }

}