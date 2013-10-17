package org.openskye.hadoop.metadata;

import org.openskye.core.ObjectMetadata;
import org.openskye.domain.Domain;
import org.openskye.domain.Project;
import org.openskye.metadata.ObjectMetadataSearch;
import org.openskye.util.Page;

/**
 * An implementation of the {@link ObjectMetadataSearch} that uses HBase as the
 * storage solution
 */
public class HBaseObjectMetadataSearch implements ObjectMetadataSearch {
    @Override
    public Iterable<ObjectMetadata> search(Domain domain, String query, Page page) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<ObjectMetadata> search(Domain domain, Project project, String query, Page page) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void index(ObjectMetadata objectMetadata) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
