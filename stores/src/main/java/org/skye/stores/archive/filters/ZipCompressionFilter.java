package org.skye.stores.archive.filters;

import org.skye.core.ObjectStreamFilter;
import org.skye.domain.DomainArchiveStore;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import java.util.zip.InflaterOutputStream;

/**
 * A Zip compression implementation of an {@link org.skye.core.ObjectStreamFilter}
 */
public class ZipCompressionFilter implements ObjectStreamFilter {
    @Override
    public void initialize(DomainArchiveStore domainArchiveStore) {
        // nothing to do
    }

    @Override
    public String getSuffix() {
        return ".zip";
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