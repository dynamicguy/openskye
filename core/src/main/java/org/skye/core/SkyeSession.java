package org.skye.core;

import org.apache.shiro.SecurityUtils;
import org.skye.domain.Domain;
import org.skye.domain.User;

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
