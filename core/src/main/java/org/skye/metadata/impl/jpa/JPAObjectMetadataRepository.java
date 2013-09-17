package org.skye.metadata.impl.jpa;

import com.google.common.base.Optional;
import org.skye.core.ObjectMetadata;
import org.skye.core.ObjectSet;
import org.skye.domain.InformationStoreDefinition;
import org.skye.domain.Task;
import org.skye.metadata.ObjectMetadataRepository;

/**
 * An implementation of the {@link ObjectMetadataRepository}
 */
public class JPAObjectMetadataRepository implements ObjectMetadataRepository
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
    public void addObjectToSet(ObjectSet objectSet, ObjectMetadata objectMetadata) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void removeObjectToSet(ObjectSet objectSet, ObjectMetadata objectMetadata) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<ObjectMetadata> get(String id)
    {
        return null;
    }

    @Override
    public void put(ObjectMetadata objectMetadata)
    {
        return;
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(InformationStoreDefinition informationStoreDefinition)
    {
        return null;
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(Task task)
    {
        return null;
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(ObjectSet objectSet) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
