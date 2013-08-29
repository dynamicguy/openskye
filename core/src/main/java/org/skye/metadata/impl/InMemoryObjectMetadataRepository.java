package org.skye.metadata.impl;

import com.google.common.base.Optional;
import org.skye.core.ArchiveContentBlock;
import org.skye.core.SimpleObject;
import org.skye.domain.DomainInformationStore;
import org.skye.metadata.ObjectMetadataRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * An in-memory object metadata repository that can be used for testing
 */
public class InMemoryObjectMetadataRepository implements ObjectMetadataRepository {

    private Map<String, SimpleObject> objects = new HashMap<>();

    @Override
    public Optional<SimpleObject> get(String id) {
        if (objects.containsKey(id))
            return Optional.of(objects.get(id));
        else
            return Optional.absent();
    }

    @Override
    public void put(SimpleObject simpleObject) {
        simpleObject.setId(UUID.randomUUID().toString());
        objects.put(simpleObject.getId(), simpleObject);
    }

    @Override
    public Iterable<ArchiveContentBlock> getArchiveContentBlocks(SimpleObject simpleObject) {
        return simpleObject.getArchiveContentBlocks();
    }

    @Override
    public Iterable<SimpleObject> getSimpleObjects(DomainInformationStore domainInformationStore) {
        return objects.values();
    }
}
