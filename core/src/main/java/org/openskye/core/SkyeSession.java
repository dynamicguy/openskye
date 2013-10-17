package org.openskye.core;

import org.apache.shiro.SecurityUtils;
import org.openskye.domain.Domain;
import org.openskye.domain.User;

public class SkyeSession
{
    public User getUser()
    {
        return (User) SecurityUtils.getSubject().getPrincipal();
    }

    public Domain getDomain()
    {
        return this.getUser().getDomain();
    }
}
