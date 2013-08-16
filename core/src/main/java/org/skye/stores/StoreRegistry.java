package org.skye.stores;

import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.skye.core.ArchiveStore;
import org.skye.core.InformationStore;
import org.skye.core.SkyeException;
import org.skye.domain.DomainArchiveStore;
import org.skye.domain.DomainInformationStore;

import java.util.HashSet;
import java.util.Set;

/**
 * A simple registry for the {@link org.skye.core.InformationStore} and {@link org.skye.core.ArchiveStore}
 * instances within a running instance of Skye
 */
@Slf4j
public class StoreRegistry {

    // These are local caches of the stores that we
    // use to allow registry
    private Set<ArchiveStore> archiveStores = new HashSet<>();
    private Set<InformationStore> informationStores = new HashSet<>();

    /**
     * Default constructor will look for all implementations of stores on the classpath
     * and then load them up into the registry
     */
    public StoreRegistry() {
        Reflections reflections = new Reflections(this.getClass().getPackage().getName());

        // Get all the ArchiveStore and InformationStore implementations
        for (Class<? extends ArchiveStore> archiveStoreClass : reflections.getSubTypesOf(ArchiveStore.class)) {
            try {
                archiveStores.add(archiveStoreClass.newInstance());
            } catch (Exception e) {
                log.error("Unable to create an instance of " + archiveStoreClass.getName(), e);
            }
        }

        for (Class<? extends InformationStore> informationStoreClass : reflections.getSubTypesOf(InformationStore.class)) {
            try {
                informationStores.add(informationStoreClass.newInstance());
            } catch (Exception e) {
                log.error("Unable to create an instance of " + informationStoreClass.getName(), e);
            }
        }

    }

    /**
     * Given a {@link DomainInformationStore} it is resolve the implementation and then
     * return an instance of the {@link InformationStore}
     *
     * @param dis The DomainInformationStore
     * @return A new instance of the relevant InformationStore
     */
    public Optional<InformationStore> build(DomainInformationStore dis) {
        log.debug("Creating new information store for " + dis);
        for (InformationStore is : informationStores) {
            if (is.isImplementing(dis.getImplementation())) {
                // Spin up a new instance of the InformationStore
                try {
                    InformationStore newInstance = is.getClass().newInstance();
                    return Optional.of(newInstance);
                } catch (Exception e) {
                    throw new SkyeException("Unable to build a new instance of the information store " + is.getName(), e);
                }
            }
        }
        log.debug("Unable to find information store for " + dis);
        return Optional.absent();
    }

    /**
     * Given a {@link DomainArchiveStore} it is resolve the implementation and then
     * return an instance of the {@link ArchiveStore}
     *
     * @param das The DomainArchiveStore
     * @return A new instance of the relevant ArchiveStore
     */
    public Optional<ArchiveStore> build(DomainArchiveStore das) {
        log.debug("Creating new archive store for " + das);
        for (ArchiveStore is : archiveStores) {
            if (is.isImplementing(das.getArchiveStoreInstance().getImplementation())) {
                // Spin up a new instance of the InformationStore
                try {
                    ArchiveStore newInstance = is.getClass().newInstance();
                    return Optional.of(newInstance);
                } catch (Exception e) {
                    throw new SkyeException("Unable to build a new instance of the archive store " + is.getName(), e);
                }
            }
        }
        log.debug("Unable to find archive store for " + das);

        return Optional.absent();
    }

    /**
     * Return the number of information stores that are registered
     *
     * @return number of information stores
     */
    public int getInformationStoreCount() {
        return informationStores.size();
    }

    /**
     * Return the number of archive stores that are registered
     *
     * @return number of archive stores
     */
    public int getArchiveStoreCount() {
        return informationStores.size();
    }
}
