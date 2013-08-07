package org.skye;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.hibernate.HibernateBundle;
import com.yammer.metrics.core.HealthCheck;
import lombok.extern.slf4j.Slf4j;
import org.skye.config.SkyeConfiguration;
import org.skye.config.SkyeSpringConfiguration;
import org.skye.domain.*;
import org.skye.util.SpringContextLoaderListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

/**
 * The Skye Service
 */
@Slf4j
public class SkyeService extends Service<SkyeConfiguration> {

    protected final HibernateBundle<SkyeConfiguration> hibernate = new HibernateBundle<SkyeConfiguration>(Domain.class, Channel.class, Project.class, User.class, UserRole.class, Role.class, Permission.class, ChannelArchiveStore.class, DomainArchiveStore.class, ArchiveStoreInstance.class) {
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
        bootstrap.addBundle(new AssetsBundle("/apidocs", "/explore", "index.html"));
    }

    @Override
    public void run(SkyeConfiguration configuration,
                    Environment environment) {
        loadSpringContext(configuration, environment, hibernate);
    }

    protected void loadSpringContext(SkyeConfiguration configuration,
                                     Environment environment, HibernateBundle<SkyeConfiguration> hibernate) {
        AnnotationConfigWebApplicationContext parent = new AnnotationConfigWebApplicationContext();
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();

        parent.refresh();
        parent.getBeanFactory().registerSingleton("configuration", configuration);
        parent.getBeanFactory().registerSingleton("hibernateSessionFactory", hibernate.getSessionFactory());
        parent.registerShutdownHook();
        parent.start();

        ctx.setParent(parent);
        ctx.register(SkyeSpringConfiguration.class);
        ctx.refresh();
        ctx.registerShutdownHook();
        ctx.start();

        for (HealthCheck entry : ctx.getBeansOfType(HealthCheck.class).values()) {
            log.info("Adding health check " + entry);
            environment.addHealthCheck(entry);
        }
        for (Object entry : ctx.getBeansWithAnnotation(Path.class).values()) {
            log.info("Adding resource " + entry);
            environment.addResource(entry);
        }
        for (com.yammer.dropwizard.tasks.Task entry : ctx.getBeansOfType(com.yammer.dropwizard.tasks.Task.class).values()) {
            log.info("Adding task " + entry);
            environment.addTask(entry);
        }
        for (Object entry : ctx.getBeansWithAnnotation(Provider.class).values()) {
            log.info("Adding provider " + entry);
            environment.addProvider(entry);
        }

        environment.addServletListeners(new SpringContextLoaderListener(ctx));
        environment.addFilter(DelegatingFilterProxy.class, "/*").setName("springSecurityFilterChain");
    }

}