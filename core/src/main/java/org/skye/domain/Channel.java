package org.skye.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The representation of a channel within Skye
 */
@Entity
@Table(name = "CHANNEL")
public class Channel extends AbstractDomainObject {

    private Project project;
    private String name;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
