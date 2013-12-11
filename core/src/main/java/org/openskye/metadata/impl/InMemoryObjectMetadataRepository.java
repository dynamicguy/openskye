package org.openskye.metadata.impl;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.openskye.core.ArchiveContentBlock;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.ObjectSet;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.domain.Task;
import org.openskye.metadata.ObjectMetadataRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An in-memory object metadata repository that can be used for testing
 */
public class InMemoryObjectMetadataRepository implements ObjectMetadataRepository {

    private Map<String, ObjectMetadata> objects = new HashMap<>();
    private Map<String, List<ObjectMetadata>> taskMap = new HashMap<>();

    @Override
    public ObjectSet createObjectSet(String name) {
        // TODO needs implementing
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteObjectSet(ObjectSet objectSet) {
        // TODO needs implementing
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<ArchiveContentBlock> getArchiveContentBlock(String checksum, long originalSize) {
        return Optional.absent();
    }

    @Override
    public void addObjectToSet(ObjectSet objectSet, ObjectMetadata objectMetadata) {
        // TODO needs implementing
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeObjectFromSet(ObjectSet objectSet, ObjectMetadata objectMetadata) {
        // TODO needs implementing
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isObjectInSet(ObjectSet objectSet, ObjectMetadata objectMetadata) {
        return false;
    }

    @Override
    public Optional<ObjectMetadata> get(String id) {
        if (objects.containsKey(id))
            return Optional.of(objects.get(id));
        else
            return Optional.absent();
    }

    @Override
    public ObjectMetadata put(ObjectMetadata objectMetadata) {
        objects.put(objectMetadata.getId(), objectMetadata);
        if (!taskMap.containsKey(objectMetadata.getTaskId())) {
            taskMap.put(objectMetadata.getTaskId(), new ArrayList<ObjectMetadata>());
        }

        taskMap.get(objectMetadata.getTaskId()).add(objectMetadata);

        return objectMetadata;
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(InformationStoreDefinition informationStoreDefinition) {
        List<ObjectMetadata> matchingObjects = new ArrayList<ObjectMetadata>();
        for (ObjectMetadata objectMetadata : objects.values()) {
            if (objectMetadata.getInformationStoreId().equals(informationStoreDefinition.getId())) {
                matchingObjects.add(objectMetadata);
            }
        }
        return matchingObjects;
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(Task task) {
        if (taskMap.containsKey(task.getId())) {
            return new ArrayList<>();
        } else {
            return Lists.newArrayList(taskMap.get(task.getId()));
        }
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(ObjectSet objectSet) {
        // TODO needs implementing
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<ObjectSet> getObjectSet(String objectSetId) {
        // TODO needs implementing
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<ObjectSet> getAllObjectSets() {
        // TODO needs implementing
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterable<ObjectMetadata> getAllObjects() {
        return this.objects.values();
    }

    @Override
    public InformationStoreDefinition getSourceInformationStoreDefinition(ObjectMetadata objectMetadata) {
        // TODO needs implementing
        throw new UnsupportedOperationException();
    }

    @Override
    public ArchiveStoreDefinition getArchiveStoreDefinition(ArchiveContentBlock archiveContentBlock) {
        // TODO needs implementing
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateObjectSet(Optional<ObjectSet> objectSet) {
        // TODO needs implementing
        throw new UnsupportedOperationException();
    }
}
