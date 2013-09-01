package org.skye.stores.inmemory;

import org.omg.CORBA.portable.InputStream;
import org.skye.core.ArchiveStore;
import org.skye.core.ArchiveStoreWriter;
import org.skye.core.ObjectMetadata;
import org.skye.core.SimpleObject;
import org.skye.domain.DomainArchiveStore;
import org.skye.domain.Task;

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
    public ArchiveStoreWriter getWriter(Task task) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InputStream getStream(ObjectMetadata simpleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
