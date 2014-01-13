package org.openskye.stores.archive.host;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openskye.core.MissingObjectException;
import org.openskye.core.UnstructuredObject;

import java.io.InputStream;

/**
 * An implementation of an {@link org.openskye.core.UnstructuredObject} that can be used
 * with files stored on a {@link org.openskye.stores.archive.host.HostArchiveStore}
 */
@Slf4j
public class HostArchiveUnstructuredObject extends UnstructuredObject {
    @Getter
    @Setter
    private HostArchiveStore archiveStore = null;

    @Override
    public InputStream getInputStream() throws MissingObjectException {
        return archiveStore.getStream(this.getObjectMetadata()).get();
    }
}