package org.openskye.stores.archive;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openskye.core.ArchiveStoreWriter;
import org.openskye.core.ObjectStreamFilter;
import org.openskye.core.SimpleObject;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.metadata.ObjectMetadataSearch;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * A base implementation of the {@link ArchiveStoreWriter} that handles
 * the OMR/OMS calls
 */
@Slf4j
public abstract class AbstractArchiveStoreWriter implements ArchiveStoreWriter {
    @Inject
    @Getter
    private ObjectMetadataRepository omr;
    @Inject
    @Getter
    private ObjectMetadataSearch oms;

    public void updateMetadata(SimpleObject simpleObject) {
        if (log.isDebugEnabled())
            log.debug("Updating metadata for " + simpleObject);
        omr.put(simpleObject.getObjectMetadata());
        oms.index(simpleObject.getObjectMetadata());
    }

    protected InputStream processFilters(Iterable<ObjectStreamFilter> filters, FileInputStream inputStream) {
        InputStream lastInputStream = inputStream;
        for (ObjectStreamFilter filter : filters) {
            lastInputStream = filter.process(inputStream);
        }
        return lastInputStream;
    }

}
