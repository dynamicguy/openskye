package org.skye.stores.inmemory;

import org.omg.CORBA.portable.InputStream;
import org.skye.core.ArchiveStore;
import org.skye.core.SimpleObject;
import org.skye.domain.DomainArchiveStore;

import java.util.HashMap;
import java.util.Map;

/**
 * An in-memory {@link org.skye.core.ArchiveStore} that can be used for testing
 */
public class InMemoryArchiveStore implements ArchiveStore {

    public static final String IMPLEMENTATION = "In-memory";
    private Map<SimpleObject, SimpleObject> objects = new HashMap<>();

    @Override
    public void initialize(DomainArchiveStore das) {

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
        return true;
    }

    @Override
    public boolean isImplementing(String implementation) {
        return implementation.equals(IMPLEMENTATION);
    }

    @Override
    public void put(SimpleObject simpleObject) {
        objects.put(simpleObject, simpleObject);
    }

    @Override
    public InputStream getStream(SimpleObject simpleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
