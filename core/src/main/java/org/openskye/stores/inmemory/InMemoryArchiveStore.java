package org.openskye.stores.inmemory;

import com.google.common.base.Optional;
import org.openskye.core.*;
import org.openskye.domain.ArchiveStoreInstance;
import org.openskye.domain.Task;
import org.openskye.replicate.Replicator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An in-memory {@link org.openskye.core.ArchiveStore} that can be used for testing
 */
public class InMemoryArchiveStore implements ArchiveStore, ArchiveStoreWriter {

    public static final String IMPLEMENTATION = "In-memory";
    private Map<String, SimpleObject> objects = new HashMap<>();
    private ArchiveStoreInstance archiveStoreInstance;

    @Override
    public void initialize(ArchiveStoreInstance asi) {
        this.archiveStoreInstance = asi;
    }

    @Override
    public String getName() {
        return "In-memory (for testing only)";
    }

    @Override
    public String getImplementation() {
        return IMPLEMENTATION;
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
    public Optional<InputStream> getStream(ObjectMetadata simpleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<SimpleObject> materialize(ObjectMetadata metadata) {
        if (objects.containsKey(metadata.getId())) {
            return Optional.of(objects.get(metadata.getId()));
        } else
            return Optional.absent();
    }

    @Override
    public boolean verify(ArchiveContentBlock acb) {
        return true;
    }

    @Override
    public Iterable<ObjectStreamFilter> getFilters() {
        return new ArrayList<>();
    }

    @Override
    public SimpleObject put(SimpleObject simpleObject) {
        if (objects.containsKey(simpleObject.getObjectMetadata().getId())) {
            throw new SkyeException("Simple Object already archived?");
        } else {
            objects.put(simpleObject.getObjectMetadata().getId(), simpleObject);
        }
        return simpleObject;
    }

    @Override
    public boolean isObjectArchived(SimpleObject simpleObject) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public UnstructuredCompressedObject compress(SimpleObject so) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void compress(ArchiveContentBlock acb) {

    }

    @Override
    public void compressAllACBs() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<SimpleObject> decompress(UnstructuredCompressedObject compressedObject) {
        return null;
    }

    @Override
    public void close() {
        // nothing to do
    }

    @Override
    public ArchiveStoreInstance getArchiveStoreInstance() {
        return archiveStoreInstance;
    }

    @Override
    public void destroy(ObjectMetadata om) {
        objects.remove(om);
    }

    @Override
    public Optional<Replicator> getReplicator() {
        return Optional.absent();
    }

}
