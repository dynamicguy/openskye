package org.skye.metadata.impl;

import com.google.common.base.Optional;
import org.skye.core.ObjectMetadata;
import org.skye.domain.DomainInformationStore;
import org.skye.metadata.ObjectMetadataRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * An in-memory object metadata repository that can be used for testing
 */
public class InMemoryObjectMetadataRepository implements ObjectMetadataRepository {

    private Map<String, ObjectMetadata> objects = new HashMap<>();

    @Override
    public Optional<ObjectMetadata> get(String id) {
        if (objects.containsKey(id))
            return Optional.of(objects.get(id));
        else
            return Optional.absent();
    }

    @Override
    public void put(ObjectMetadata objectMetadata) {
        objects.put(objectMetadata.getId(), objectMetadata);
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(DomainInformationStore domainInformationStore) {
        return objects.values();
    }
}
