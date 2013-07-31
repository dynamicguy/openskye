package org.skye.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Relationship between {@link User} and {@link Role}
 */
@Entity
@Table(name = "USER_ROLE")
public class UserRole extends AbstractDomainObject {

    private User user;
    private Role role;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
