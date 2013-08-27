package org.skye.stores.archive.localfs;

import lombok.extern.slf4j.Slf4j;
import org.skye.core.ArchiveStoreWriter;
import org.skye.core.SimpleObject;
import org.skye.domain.Task;
import org.skye.stores.archive.AbstractArchiveStoreWriter;

/**
 * An implementation of an {@link ArchiveStoreWriter} for the {@link LocalFilesystemArchiveStore}
 */
@Slf4j
public class LocalFilesystemArchiveWriter extends AbstractArchiveStoreWriter {
    private final Task task;
    private final LocalFilesystemArchiveStore localFilesystemArchiveStore;

    public LocalFilesystemArchiveWriter(Task task, LocalFilesystemArchiveStore localFilesystemArchiveStore) {
        this.localFilesystemArchiveStore = localFilesystemArchiveStore;
        this.task = task;
    }

    @Override
    public void put(SimpleObject simpleObject) {
        updateMetadata(simpleObject);
    }

    @Override
    public void close() {
        // nothing to do
    }
}
