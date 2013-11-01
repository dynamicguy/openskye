package org.openskye.metadata.impl.jpa;

import com.google.inject.persist.PersistService;

import javax.inject.Inject;

/**
 * A helper for initializing the JPA environment
 */
public class JpaInitializer {
    @Inject
    public JpaInitializer(final PersistService service) {
        service.start();
    }
}
