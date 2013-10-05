package org.skye.hadoop.metadata;

import org.skye.core.ObjectMetadata;
import org.skye.domain.Domain;
import org.skye.domain.Project;
import org.skye.metadata.ObjectMetadataSearch;
import org.skye.util.Page;

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
