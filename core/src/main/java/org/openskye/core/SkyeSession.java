package org.openskye.core;

import org.apache.shiro.SecurityUtils;
import org.openskye.domain.Domain;
import org.openskye.domain.User;

/**
 * A helper that can be used to identify the current user's session
 */
public class SkyeSession {
    public User getUser() {
        return (User) SecurityUtils.getSubject().getPrincipal();
    }

    public Domain getDomain() {
        return getUser().getDomain();
    }
}
