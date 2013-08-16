package org.skye.stores;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * The representation of the metadata for the {@link StoreRegistry}
 */
@Data
public class StoreRegistryMetadata {

    private Set<RegisteredArchiveStore> archiveStores = new HashSet<>();
    private Set<RegisteredInformationStore> informationStores = new HashSet<>();
}
