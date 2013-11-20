package org.openskye.stores;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * The representation of the metadata for the {@link StoreRegistry}
 */
@Data
public class StoreRegistryMetadata {
    @JsonInclude
    private Set<RegisteredArchiveStore> archiveStores = new HashSet<>();
    @JsonInclude
    private Set<RegisteredInformationStore> informationStores = new HashSet<>();
}
