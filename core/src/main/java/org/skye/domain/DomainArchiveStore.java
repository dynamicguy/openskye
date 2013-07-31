package org.skye.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A {@link Domain} owned {@link ArchiveStoreInstance}
 */
@Entity
@Table(name = "DOMAIN_ARCHIVE_STORE")
public class DomainArchiveStore extends AbstractDomainObject {

    private Domain domain;
    private ArchiveStoreInstance archiveStoreInstance;

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public ArchiveStoreInstance getArchiveStoreInstance() {
        return archiveStoreInstance;
    }

    public void setArchiveStoreInstance(ArchiveStoreInstance archiveStoreInstance) {
        this.archiveStoreInstance = archiveStoreInstance;
    }
}
