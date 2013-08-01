package org.skye;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.hibernate.HibernateBundle;
import org.skye.config.SkyeConfiguration;
import org.skye.domain.*;
import org.skye.resource.*;
import org.skye.resource.dao.ChannelDAO;
import org.skye.resource.dao.DomainDAO;
import org.skye.resource.dao.ProjectDAO;
import org.skye.resource.dao.UserDAO;

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

        // We will pick up the Doclet generated swagger configuration
        bootstrap.addBundle(new AssetsBundle("/apidocs", "/apidocs", "index.html"));
    }

    @Override
    public void run(SkyeConfiguration configuration,
                    Environment environment) {
        environment.addResource(new PlatformResource());
        environment.addResource(new DomainResource(new DomainDAO(hibernate.getSessionFactory())));
        environment.addResource(new ChannelResource(new ChannelDAO(hibernate.getSessionFactory())));
        environment.addResource(new UserResource(new UserDAO(hibernate.getSessionFactory())));
        environment.addResource(new ProjectResource(new ProjectDAO(hibernate.getSessionFactory())));


    }

}