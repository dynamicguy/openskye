package org.openskye.guice;


import com.google.common.base.Preconditions;
import com.google.inject.Injector;
import com.sun.jersey.spi.inject.InjectableProvider;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.servlets.tasks.Task;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;
import java.util.Set;

public class AutoConfig {

    final Logger logger = LoggerFactory.getLogger(AutoConfig.class);
    private Reflections reflections;

    public AutoConfig(String... basePackages) {
        Preconditions.checkArgument(basePackages.length > 0);

        ConfigurationBuilder cfgBldr = new ConfigurationBuilder();
        FilterBuilder filterBuilder = new FilterBuilder();
        for (String basePkg : basePackages) {
            cfgBldr.addUrls(ClasspathHelper.forPackage(basePkg));
            filterBuilder.include(FilterBuilder.prefix(basePkg));
        }

        cfgBldr.filterInputsBy(filterBuilder).setScanners(
                new SubTypesScanner(), new TypeAnnotationsScanner());
        this.reflections = new Reflections(cfgBldr);
    }

    public void run(Environment environment, Injector injector) {
        addProviders(environment);
        addInjectableProviders(environment);
        addResources(environment);
        addTasks(environment, injector);
        addManaged(environment, injector);
    }

    public void addManaged(Environment environment, Injector injector) {
        Set<Class<? extends Managed>> managedClasses = reflections
                .getSubTypesOf(Managed.class);
        for (Class<? extends Managed> managed : managedClasses) {
            environment.lifecycle().manage(injector.getInstance(managed));
            logger.info("Added managed: {}", managed);
        }
    }

    public void addTasks(Environment environment, Injector injector) {
        Set<Class<? extends Task>> taskClasses = reflections
                .getSubTypesOf(Task.class);
        for (Class<? extends Task> task : taskClasses) {
            environment.admin().addTask(injector.getInstance(task));
            logger.info("Added task: {}", task);
        }
    }

    @SuppressWarnings("rawtypes")
    public void addInjectableProviders(Environment environment) {
        Set<Class<? extends InjectableProvider>> injectableProviders = reflections
                .getSubTypesOf(InjectableProvider.class);
        for (Class<? extends InjectableProvider> injectableProvider : injectableProviders) {
            environment.jersey().register(injectableProvider);
            logger.info("Added injectableProvider: {}", injectableProvider);
        }
    }

    public void addProviders(Environment environment) {
        Set<Class<?>> providerClasses = reflections
                .getTypesAnnotatedWith(Provider.class);
        for (Class<?> provider : providerClasses) {
            environment.jersey().register(provider);
            logger.info("Added provider class: {}", provider);
        }
    }

    public void addResources(Environment environment) {
        Set<Class<?>> resourceClasses = reflections
                .getTypesAnnotatedWith(Path.class);
        for (Class<?> resource : resourceClasses) {
            environment.jersey().register(resource);
            logger.info("Added resource class: {}", resource);
        }
    }

}

