package org.skye.hadoop.metadata;

import com.google.common.base.Optional;
import org.skye.core.ObjectMetadata;
import org.skye.domain.DomainInformationStore;
import org.skye.domain.Task;
import org.skye.metadata.ObjectMetadataRepository;

/**
 * An implementation of the {@link ObjectMetadataRepository} that uses HBase as the
 * storage solution
 */
public class HBaseObjectMetadataRepository implements ObjectMetadataRepository {

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
