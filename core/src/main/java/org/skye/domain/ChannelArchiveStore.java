package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * The representation of a relationship that exists between a channel and an archive store
 */
@Entity
@Table(name = "CHANNEL")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChannelArchiveStore {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;
    @ManyToOne
    private Channel channel;
    @ManyToOne
    private DomainArchiveStore domainArchiveStore;

}
