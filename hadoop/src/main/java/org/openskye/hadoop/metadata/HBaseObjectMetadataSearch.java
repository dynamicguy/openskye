package org.openskye.hadoop.metadata;

import org.openskye.core.ObjectMetadata;
import org.openskye.domain.Domain;
import org.openskye.domain.Project;
import org.openskye.metadata.ObjectMetadataSearch;

/**
 * An implementation of the {@link ObjectMetadataSearch} that uses HBase as the
 * storage solution
 */
public class HBaseObjectMetadataSearch implements ObjectMetadataSearch {

    @Override
    public Iterable<ObjectMetadata> search(Domain domain, String query) {
        return null;
    }

    @Override
    public Iterable<ObjectMetadata> search(Domain domain, Project project, String query) {
        return null;
    }

    @Override
    public void index(ObjectMetadata objectMetadata) {

        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Clears the indexed items for the OMS.
     * <p/>
     * This is intended for testing and demo purposes.
     */
    @Override
    public void clear() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Ensures that all indexed entries are added to internal storage.
     * <p/>
     * This is intended for testing and demo purposes.
     */
    @Override
    public void flush() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
