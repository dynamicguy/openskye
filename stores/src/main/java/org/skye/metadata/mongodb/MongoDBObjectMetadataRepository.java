package org.skye.metadata.mongodb;

import com.google.common.base.Optional;
import org.skye.core.ObjectMetadata;
import org.skye.domain.DomainInformationStore;
import org.skye.domain.Task;
import org.skye.metadata.ObjectMetadataRepository;

/**
 * An implementation of the {@link ObjectMetadataRepository} based on MongoDB
 */
public class MongoDBObjectMetadataRepository implements ObjectMetadataRepository {

    @Override
    public Optional<ObjectMetadata> get(String id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void put(ObjectMetadata objectMetadata) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(DomainInformationStore domainInformationStore) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<ObjectMetadata> getObjects(Task task) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
