package org.skye.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * A {@link Domain} owned {@link ArchiveStoreInstance}
 */
@Entity
@Table(name = "DOMAIN_ARCHIVE_STORE")
public class DomainArchiveStore {

    @Id
    @GeneratedValue(generator = "hibernate-uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    private String id;
    private Domain domain;
    private ArchiveStoreInstance archiveStoreInstance;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
