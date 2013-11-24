package org.openskye.core;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.openskye.domain.Domain;
import org.openskye.domain.User;

/**
 * A helper that can be used to identify the current user's session
 */
public class SkyeSession {
    public User getUser() {
        try {
            return (User) SecurityUtils.getSubject().getPrincipal();
        } catch (UnavailableSecurityManagerException e) {
            return null;
        }
    }

    public Domain getDomain() {
        User user = getUser();
        if (user != null)
            return getUser().getDomain();
        else
            return null;
    }
}
