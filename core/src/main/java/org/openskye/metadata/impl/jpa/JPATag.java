package org.openskye.metadata.impl.jpa;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openskye.core.Tag;

import javax.persistence.Embeddable;

/**
 * An embeddable version of the {@link Tag} class, complete with methods to
 * translate between the standard version and the JPA embeddable version, and
 * visa versa.
 */
@Embeddable
@Data
@EqualsAndHashCode(of = "name")
public class JPATag {
    private String name;

    public JPATag() {
        this.name = "";

        return;
    }

    public JPATag(Tag tag) {
        this.name = tag.getName();

        return;
    }

    public Tag ToTag() {
        Tag tag = new Tag();

        tag.setName(this.name);

        return tag;
    }
}
