package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(of = "id")
public class ChannelArchiveStore implements Identifiable {
    @Id
    @GeneratedValue(generator = "ChannelArchiveStoreGenerator")
    @Column(unique = true, length = 36)
    private String id;
    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "CHANNEL_ID")
    @JsonIgnore
    private Channel channel;
    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "ARCHIVE_STORE_DEFINITION_ID")
    @JsonIgnore
    private ArchiveStoreDefinition archiveStoreDefinition;

}
