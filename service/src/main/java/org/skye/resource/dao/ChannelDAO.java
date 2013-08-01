package org.skye.resource.dao;

import org.hibernate.SessionFactory;
import org.skye.domain.Channel;

/**
 * DAO for the {@link org.skye.domain.Channel}
 */
public class ChannelDAO extends AbstractPaginatingDAO<Channel> {

    public ChannelDAO(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
