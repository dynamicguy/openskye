package org.skye.stores.inmemory;

import com.google.common.base.Optional;
import org.skye.core.*;
import org.skye.domain.DomainArchiveStore;
import org.skye.domain.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * An in-memory {@link org.skye.core.ArchiveStore} that can be used for testing
 */
public class InMemoryArchiveStore implements ArchiveStore, ArchiveStoreWriter {

    public static final String IMPLEMENTATION = "In-memory";
    private Map<String, SimpleObject> objects = new HashMap<>();

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
        return this;
    }

    @Override
    public java.io.InputStream getStream(ObjectMetadata simpleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<SimpleObject> getSimpleObject(ObjectMetadata metadata) {
        if (objects.containsKey(metadata.getId())) {
            return Optional.of(objects.get(metadata.getId()));
        } else
            return Optional.absent();
    }

    @Override
    public Iterable<ObjectStreamFilter> getFilters() {
        return new ArrayList<>();
    }

    @Override
    public void put(SimpleObject simpleObject) {
        if (objects.containsKey(simpleObject.getObjectMetadata().getId())) {
            throw new SkyeException("Simple Object already archived?");
        } else {
            objects.put(simpleObject.getObjectMetadata().getId(), simpleObject);
        }
    }

    @Override
    public void close() {
        // nothing to do
    }
}
