package org.skye.core;

import org.skye.domain.DomainArchiveStore;

import java.io.InputStream;

/**
 * A filter that will be applied to a {@SimpleObject}s stream during the persistence in an
 * archive store
 * <p/>
 * Note that this allows for unstructured (and structured data in some cases such as local fs store) to be
 * encrypted, compressed etc
 */
public interface ObjectStreamFilter {

    void initialize(DomainArchiveStore domainArchiveStore);

    String getSuffix();

    InputStream process(InputStream inputStream);

    InputStream unprocess(InputStream inputStream);
}
