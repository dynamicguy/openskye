package org.skye.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A project
 */
@Entity
@Table(name = "PROJECT")
public class Project extends AbstractDomainObject {

    private Domain domain;
    private String name;

    public Domain getDomain() {
        return domain;
    }

    public void setDomain(Domain domain) {
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
