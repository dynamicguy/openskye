package org.openskye.stores;

import lombok.Data;
import org.openskye.core.InformationStore;

/**
 * Data for a registered {@link org.openskye.core.InformationStore}
 */
@Data
public class RegisteredInformationStore {

    private String name;
    private String implementation;

    public RegisteredInformationStore(InformationStore informationStore) {
        this.name = informationStore.getName();
        this.implementation = informationStore.getImplementation();

    }
}
