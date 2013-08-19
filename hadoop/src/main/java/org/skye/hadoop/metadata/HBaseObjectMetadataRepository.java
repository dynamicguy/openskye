package org.skye.hadoop.metadata;

import com.google.common.base.Optional;
import org.skye.core.ArchiveContentBlock;
import org.skye.core.SimpleObject;
import org.skye.domain.Domain;
import org.skye.domain.DomainInformationStore;
import org.skye.domain.Project;
import org.skye.metadata.ObjectMetadataRepository;
import org.skye.util.Page;

import java.io.InputStream;

/**
 * An implementation of the {@link ObjectMetadataRepository} that uses HBase as the
 * storage solution
 */
public class HBaseObjectMetadataRepository implements ObjectMetadataRepository {
    @Override
    public Optional<SimpleObject> get(String id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void put(SimpleObject simpleObject) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<SimpleObject> search(Domain domain, String query, Page page) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<SimpleObject> search(Domain domain, Project project, String query, Page page) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<ArchiveContentBlock> getArchiveContentBlocks(SimpleObject simpleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InputStream getContent(SimpleObject simpleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<SimpleObject> getSimpleObjects(DomainInformationStore domainInformationStore) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
