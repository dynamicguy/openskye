package org.skye.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * A permission
 */
@Entity
@Table(name = "PERMISSION")
public class Permission {

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
}
