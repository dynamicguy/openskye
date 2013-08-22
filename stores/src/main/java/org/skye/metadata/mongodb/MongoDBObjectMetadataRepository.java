package org.skye.metadata.mongodb;

import com.google.common.base.Optional;
import org.skye.core.ArchiveContentBlock;
import org.skye.core.SimpleObject;
import org.skye.domain.DomainInformationStore;
import org.skye.metadata.ObjectMetadataRepository;

/**
 * An implementation of the {@link ObjectMetadataRepository} based on MongoDB
 */
public class MongoDBObjectMetadataRepository implements ObjectMetadataRepository {
    @Override
    public Optional<SimpleObject> get(String id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void put(SimpleObject simpleObject) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<ArchiveContentBlock> getArchiveContentBlocks(SimpleObject simpleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<SimpleObject> getSimpleObjects(DomainInformationStore domainInformationStore) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
