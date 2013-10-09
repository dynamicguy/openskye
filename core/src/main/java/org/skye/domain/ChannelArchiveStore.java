package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;

/**
 * The representation of a relationship that exists between a channel and an archive store
 */
@Entity
@Table(name = "CHANNEL_ARCHIVE_STORE")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@UuidGenerator(name = "ChannelArchiveStoreGenerator")
public class ChannelArchiveStore implements Identifiable {
    @Id
    @GeneratedValue(generator = "ChannelArchiveStoreGenerator")
    @Column(unique = true)
    private String id;
    @ManyToOne
    private Channel channel;
    @ManyToOne
    private ArchiveStoreDefinition archiveStoreDefinition;

}
