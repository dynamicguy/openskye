package org.skye.stores.archive;

import lombok.extern.slf4j.Slf4j;
import org.skye.core.ArchiveStoreWriter;
import org.skye.core.ObjectStreamFilter;
import org.skye.core.SimpleObject;
import org.skye.metadata.ObjectMetadataRepository;
import org.skye.metadata.ObjectMetadataSearch;

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
    private ObjectMetadataRepository omr;
    @Inject
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
