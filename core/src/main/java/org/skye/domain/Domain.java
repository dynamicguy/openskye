package org.skye.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A domain represents a top level entity within the organization
 */
@Entity
@Table(name = "DOMAIN")
public class Domain extends AbstractDomainObject {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
