package org.openskye.metadata.mongodb;

import com.google.common.base.Optional;
import org.openskye.core.ArchiveContentBlock;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.ObjectSet;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.domain.Task;
import org.openskye.metadata.ObjectMetadataRepository;

/**
 * An implementation of the {@link ObjectMetadataRepository} based on MongoDB
 */
public class MongoDBObjectMetadataRepository implements ObjectMetadataRepository {
    @Override
    public ObjectSet createObjectSet(String name) {
        return null;
    }

    @Override
    public void deleteObjectSet(ObjectSet objectSet) {
        return;
    }

    @Override
    public void addObjectToSet(ObjectSet objectSet, ObjectMetadata objectMetadata) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isObjectInSet(ObjectSet objectSet, ObjectMetadata objectMetadata) {
        return false;
    }

    @Override
    public void removeObjectToSet(ObjectSet objectSet, ObjectMetadata objectMetadata) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<ObjectMetadata> get(String id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ObjectMetadata put(ObjectMetadata objectMetadata) {
        return null;
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(InformationStoreDefinition informationStoreDefinition) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(Task task) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
    public Iterable<ObjectSet> getAllObjectSets()
    {
        return null;
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
