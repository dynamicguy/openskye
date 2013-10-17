package org.openskye.metadata.impl;

import com.google.common.base.Optional;
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
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void deleteObjectSet(ObjectSet objectSet) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void addObjectToSet(ObjectSet objectSet, ObjectMetadata objectMetadata) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeObjectToSet(ObjectSet objectSet, ObjectMetadata objectMetadata) {
        //To change body of implemented methods use File | Settings | File Templates.
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
    public void put(ObjectMetadata objectMetadata) {
        objects.put(objectMetadata.getId(), objectMetadata);
        if (!taskMap.containsKey(objectMetadata.getTaskId())) {
            taskMap.put(objectMetadata.getTaskId(), new ArrayList<ObjectMetadata>());
        }

        taskMap.get(objectMetadata.getTaskId()).add(objectMetadata);
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(InformationStoreDefinition informationStoreDefinition) {
        return objects.values();
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(Task task) {
        if (taskMap.containsKey(task.getId())) {
            return new ArrayList<>();
        } else {
            return taskMap.get(task.getId());
        }
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(ObjectSet objectSet) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<ObjectSet> getObjectSet(String objectSetId) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InformationStoreDefinition getSourceInformationStoreDefinition(ObjectMetadata objectMetadata) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ArchiveStoreDefinition getArchiveStoreDefinition(ArchiveContentBlock archiveContentBlock) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
