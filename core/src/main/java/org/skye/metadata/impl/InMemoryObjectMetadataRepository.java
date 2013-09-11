package org.skye.metadata.impl;

import com.google.common.base.Optional;
import org.skye.core.ObjectMetadata;
import org.skye.domain.InformationStoreDefinition;
import org.skye.domain.InformationStoreDefinition;
import org.skye.domain.Task;
import org.skye.metadata.ObjectMetadataRepository;

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
}
