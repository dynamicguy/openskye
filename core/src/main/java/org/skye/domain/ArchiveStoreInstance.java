package org.skye.domain;

import com.google.common.base.Objects;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * An instance of an {@link org.skye.core.ArchiveStore} definition
 */
@Entity
@Table(name = "ARCHIVE_STORE_INSTANCE")
public class ArchiveStoreInstance {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ArchiveStoreInstance) {
            final ArchiveStoreInstance other = (ArchiveStoreInstance) obj;
            return Objects.equal(id, other.id);
        } else {
            return false;
        }
    }
}
