package org.skye.domain;

import com.google.common.base.Objects;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * The base class for domain objects that implements the basics for persistence
 */
public abstract class AbstractDomainObject {

    @Id
    @GeneratedValue(generator = "hibernate-uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    private String id;

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
        if (obj instanceof AbstractDomainObject) {
            final AbstractDomainObject other = (AbstractDomainObject) obj;
            return Objects.equal(id, other.id);
        } else {
            return false;
        }
    }

}
