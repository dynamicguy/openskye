package org.skye.stores.inmemory;

import org.skye.core.ArchiveStore;
import org.skye.core.SimpleObject;

import java.util.Properties;

/**
 * An in-memory {@link org.skye.core.ArchiveStore} that can be used for testing
 */
public class InMemoryArchiveStore implements ArchiveStore {

    public static final String IMPLEMENTATION = "In-memory";

    @Override
    public void initialize(Properties properties) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getName() {
        return "In-memory (for testing only)";
    }

    @Override
    public String getUrl() {
        return "mem://.";
    }

    @Override
    public boolean isSupported(SimpleObject so) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isImplementing(String implementation) {
        return implementation.equals(IMPLEMENTATION);
    }
}
