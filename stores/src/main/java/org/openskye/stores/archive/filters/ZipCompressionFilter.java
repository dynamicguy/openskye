package org.openskye.stores.archive.filters;

import org.openskye.core.ObjectStreamFilter;
import org.openskye.domain.ArchiveStoreDefinition;

import java.io.InputStream;
import java.util.zip.DeflaterInputStream;
import java.util.zip.InflaterInputStream;

/**
 * A Zip compression implementation of an {@link org.openskye.core.ObjectStreamFilter}
 */
public class ZipCompressionFilter implements ObjectStreamFilter {
    @Override
    public void initialize(ArchiveStoreDefinition archiveStoreDefinition) {
        // nothing to do
    }

    @Override
    public InputStream process(InputStream inputStream) {
        return new DeflaterInputStream(inputStream);
    }

    @Override
    public InputStream unprocess(InputStream inputStream) {
        return new InflaterInputStream(inputStream);
    }
}
