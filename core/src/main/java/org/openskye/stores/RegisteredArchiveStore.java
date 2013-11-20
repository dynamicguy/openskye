package org.openskye.stores;

import lombok.Data;
import org.openskye.core.ArchiveStore;

/**
 * Data for a registered {@link org.openskye.core.ArchiveStore}
 */
@Data
public class RegisteredArchiveStore {

    private String name;
    private String implementation;

    public RegisteredArchiveStore(ArchiveStore archiveStore) {
        this.name = archiveStore.getName();
        this.implementation = archiveStore.getImplementation();
    }
}
