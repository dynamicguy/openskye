package org.skye.resource.dao;

import org.skye.domain.Channel;
import org.springframework.stereotype.Repository;

import javax.inject.Singleton;

/**
 * DAO for the {@link org.skye.domain.Channel}
 */
@Repository
public class ChannelDAO extends AbstractPaginatingDAO<Channel> {

}
