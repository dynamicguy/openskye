package org.skye.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The representation of a relationship that exists between a channel and an archive store
 */
@Entity
@Table(name = "CHANNEL")
public class ChannelArchiveStore {

    private Channel channel;
    private DomainArchiveStore domainArchiveStore;

    public DomainArchiveStore getDomainArchiveStore() {
        return domainArchiveStore;
    }

    public void setDomainArchiveStore(DomainArchiveStore domainArchiveStore) {
        this.domainArchiveStore = domainArchiveStore;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

}
