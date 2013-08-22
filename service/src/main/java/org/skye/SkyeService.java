package org.skye;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.hibernate.HibernateBundle;
import lombok.extern.slf4j.Slf4j;
import org.skye.config.SkyeConfiguration;
import org.skye.domain.*;
import org.skye.guice.GuiceBundle;
import org.skye.security.SkyeGuiceServletContextListener;
import org.skye.util.SwaggerBundle;

import javax.servlet.ServletContext;

/**
 * The Skye Service
 */
@Slf4j
public class SkyeService extends Service<SkyeConfiguration> {

    protected final HibernateBundle<SkyeConfiguration> hibernate = new HibernateBundle<SkyeConfiguration>(DomainInformationStore.class, Domain.class, Channel.class, Project.class, User.class, UserRole.class, Role.class, Permission.class, ChannelArchiveStore.class, DomainArchiveStore.class, ArchiveStoreInstance.class) {
        @Override
        public DatabaseConfiguration getDatabaseConfiguration(SkyeConfiguration configuration) {
            return configuration.getDatabaseConfiguration();
        }
    };
    private GuiceBundle guiceBundle;

    public static void main(String[] args) throws Exception {
        new SkyeService().run(args);
    }

    @Override
    public void initialize(Bootstrap<SkyeConfiguration> bootstrap) {

        bootstrap.setName("skye");
        bootstrap.addBundle(hibernate);
        this.guiceBundle = buildGuiceBundle();
        bootstrap.addBundle(guiceBundle);
        bootstrap.addBundle(new AssetsBundle("/apidocs", "/explore", "index.html"));
        bootstrap.addBundle(new SwaggerBundle());

    }

    public GuiceBundle buildGuiceBundle() {
        return GuiceBundle.<SkyeConfiguration>newBuilder()
                .addModule(new SkyeTestModule(hibernate))
                .enableAutoConfig(getClass().getPackage().getName())
                .setConfigClass(SkyeConfiguration.class)
                .build();

    }

    @Override
    public void run(SkyeConfiguration configuration,
                    Environment environment) throws Exception {

        environment.addServletListeners(new SkyeGuiceServletContextListener(guiceBundle.getInjector()));

    }

}