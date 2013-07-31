package org.skye.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The representation of a user
 */
@Entity
@Table(name = "USER")
public class User extends AbstractDomainObject {

    private String username;
    private String email;
    private String name;
    private String passwordHash;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
