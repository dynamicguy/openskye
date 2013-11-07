package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * The representation of a relationship that exists between a channel and an archive store
 */
@Entity
@Table(name = "CHANNEL_ARCHIVE_STORE")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(of = "id")
public class ChannelArchiveStore implements Identifiable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CHANNEL_ID")
    @JsonBackReference("channelArchiveStores")
    private Channel channel;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ARCHIVE_STORE_DEFINITION_ID")
    private ArchiveStoreDefinition archiveStoreDefinition;

}
