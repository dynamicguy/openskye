package org.openskye.domain.dao;

import com.google.guiceberry.GuiceBerryModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.persist.jpa.JpaPersistModule;
import org.apache.bval.guice.ValidationModule;
import org.openskye.validator.UniqueValidator;

import javax.inject.Provider;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Properties;

/**
 * A guice module that sets us up with the stuff to do
 * persistence and DAO's
 */
public class H2DatabaseTestModule extends AbstractModule {
    @Override
    protected void configure() {

        JpaPersistModule jpaPersistModule = new JpaPersistModule("Default");
        Properties props = new Properties();
        props.put("javax.persistence.jdbc.url", "jdbc:h2:mem:openskye-test");
        props.put("javax.persistence.jdbc.user", "sa");
        props.put("javax.persistence.jdbc.password", "");
        props.put("javax.persistence.jdbc.driver", "org.h2.Driver");
        jpaPersistModule.properties(props);
        install(jpaPersistModule);

        install(new ValidationModule());

        install(new GuiceBerryModule());
    }
}
