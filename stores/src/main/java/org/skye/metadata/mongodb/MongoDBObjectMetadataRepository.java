package org.skye.metadata.mongodb;

import com.google.common.base.Optional;
import org.skye.core.ObjectMetadata;
import org.skye.domain.InformationStoreDefinition;
import org.skye.domain.Task;
import org.skye.metadata.ObjectMetadataRepository;
import org.skye.core.ObjectSet;

/**
 * An implementation of the {@link ObjectMetadataRepository} based on MongoDB
 */
public class MongoDBObjectMetadataRepository implements ObjectMetadataRepository
{
    @Override
    public ObjectSet createObjectSet()
    {
        return null;
    }

    @Override
    public void deleteObjectSet(ObjectSet objectSet)
    {
        return;
    }

    @Override
    public Optional<ObjectMetadata> get(String id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void put(ObjectMetadata objectMetadata) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(InformationStoreDefinition informationStoreDefinition) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(Task task) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
