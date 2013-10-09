package org.skye.healthcheck;

import com.google.inject.Provider;
import org.skye.domain.User;
import org.skye.guice.InjectableHealthCheck;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class PersistenceHealthCheck extends InjectableHealthCheck {

    @Inject
    private Provider<EntityManager> emf;

    @Override
    protected Result check() throws Exception {
        try {
            emf.get().find(User.class, "1");
            return Result.healthy();
        } catch (Exception e) {
            return Result.unhealthy("Cannot connect to persistence layer");
        }
    }

    @Override
    public String getName() {
        return "persistence-health-check";
    }
}
