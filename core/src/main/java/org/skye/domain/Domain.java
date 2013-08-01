package org.skye.domain;

import com.google.common.base.Objects;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A domain represents a top level entity within the organization
 */
@Entity
@Table(name = "DOMAIN")
public class Domain {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;
    private String name;
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<Project> projects = new ArrayList<>();

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

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
        if (obj instanceof AuditLog) {
            final AuditLog other = (AuditLog) obj;
            return Objects.equal(id, other.id);
        } else {
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
