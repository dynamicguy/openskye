package org.skye;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.hibernate.HibernateBundle;
import org.skye.config.SkyeConfiguration;
import org.skye.domain.Domain;
import org.skye.resource.DomainResource;
import org.skye.resource.PlatformResource;
import org.skye.resource.dao.DomainDAO;

/**
 * The Skye Service
 */
public class SkyeService extends Service<SkyeConfiguration> {

    private final HibernateBundle<SkyeConfiguration> hibernate = new HibernateBundle<SkyeConfiguration>(Domain.class) {
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
    }

    @Override
    public void run(SkyeConfiguration configuration,
                    Environment environment) {
        environment.addResource(new PlatformResource());
        environment.addResource(new DomainResource(new DomainDAO(hibernate.getSessionFactory())));
    }

}