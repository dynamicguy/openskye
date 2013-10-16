package org.skye.hadoop;

import com.google.inject.persist.PersistService;

import javax.inject.Inject;

/**
 * A JPA Initializer
 */
public class HBaseJPAInitializer {
    @Inject
    public HBaseJPAInitializer(final PersistService service) {
        service.start();
    }
}
