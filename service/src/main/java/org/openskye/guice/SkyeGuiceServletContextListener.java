package org.openskye.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.guice.web.GuiceShiroFilter;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.openskye.SkyeApplication;
import org.openskye.task.TaskManager;
import org.openskye.util.PersistFilter;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

/**
 * This takes care of wiring up the Guice/Shiro/Dropwizard world at the last minute
 */
@Slf4j
public class SkyeGuiceServletContextListener extends GuiceServletContextListener {
    private final JpaPersistModule jpaPersistModule;
    private final Environment environment;
    private final Bootstrap bootstrap;
    private final JerseyContainerModule jerseyContainerModule;
    private final DropwizardEnvironmentModule dropwizardEnviroment;
    private final SkyeModule skyeModule;
    private ServletContext servletContext;
    private Injector injector;

    public SkyeGuiceServletContextListener(JpaPersistModule jpaPersistModule, Environment environment, Bootstrap bootstrap, JerseyContainerModule jerseyContainerModule, DropwizardEnvironmentModule dropwizardEnvironmentModule, SkyeModule skyeModule) {
        this.jpaPersistModule = jpaPersistModule;
        this.environment = environment;
        this.bootstrap = bootstrap;
        this.jerseyContainerModule = jerseyContainerModule;
        this.dropwizardEnviroment = dropwizardEnvironmentModule;
        this.skyeModule = skyeModule;
    }

    @Override
    protected Injector getInjector() {
        if (injector == null) {
            ShiroWebModule shiroWebModule = new SkyeShiroModule(servletContext, jerseyContainerModule);
            injector = Guice.createInjector(jpaPersistModule, shiroWebModule, dropwizardEnviroment, skyeModule);
        }

        return injector;
    }

    @Override
    public void contextInitialized(final ServletContextEvent servletContextEvent) {
        this.servletContext = servletContextEvent.getServletContext();
        super.contextInitialized(servletContextEvent);
        this.servletContext.addFilter("Guice Persist Filter", getInjector().getInstance(PersistFilter.class))
                .addMappingForUrlPatterns(null, false, environment.getApplicationContext().getContextPath() + "*");

        this.servletContext.addFilter("Guice Shiro Filter", getInjector().getInstance(GuiceShiroFilter.class))
                .addMappingForUrlPatterns(null, false, environment.getApplicationContext().getContextPath() + "*");

        AutoConfig autoConfig = new AutoConfig(SkyeApplication.class.getPackage().getName());
        autoConfig.run(environment, injector);

        log.info("Starting the task manager");
        TaskManager taskManager = injector.getInstance(TaskManager.class);
        taskManager.start();
    }
}