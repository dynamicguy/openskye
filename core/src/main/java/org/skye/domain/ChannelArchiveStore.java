package org.skye.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * The representation of a relationship that exists between a channel and an archive store
 */
@Entity
@Table(name = "CHANNEL")
public class ChannelArchiveStore {

    @Id
    @GeneratedValue(generator = "hibernate-uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    private String id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
