package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * The representation of a channel within Skye. A channel is a set of rules that define the objects to extract, the
 * {@link org.openskye.core.ArchiveStore}s to extract the objects from, and the {@link
 * org.openskye.core.InformationStore} to extract the objects to.
 *
 * @see ChannelArchiveStore
 * @see org.openskye.core.InformationStore
 */
@Entity
@Table(name = "CHANNEL")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString(exclude = {"channelArchiveStores", "attributeInstances", "channelFilters"})
public class Channel implements Identifiable {
    /**
     * The <code>Channel</code> id
     */
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    /**
     * The <code>Project</code> this <code>Channel</code> is attached to.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PROJECT_ID")
    private Project project;
    /**
     * The {@link org.openskye.core.InformationStore} that files extracted using this <code>Channel</code> are extracted
     * to.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    private InformationStoreDefinition informationStoreDefinition;
    /**
     * A set of {@link org.openskye.core.ArchiveStore}s that files extracted using this <code>Channel</code> are
     * archived.
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "channel")
    @JsonManagedReference("channelArchiveStores")
    private List<ChannelArchiveStore> channelArchiveStores = new ArrayList<>();
    /**
     * The name of this <code>Channel</code>
     */
    @NotNull
    @NotBlank
    @Column(unique=true)
    private String name;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "channel")
    private List<AttributeInstance> attributeInstances = new ArrayList<>();
    /**
     * A filter applied to objects extracted using this <code>Channel</code>. Only objects which fit this filter are
     * extracted.
     */
    @JsonManagedReference("channelFilters")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "channel")
    private List<ChannelFilterDefinition> channelFilters = new ArrayList<>();
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RETENTION_POLICY_ID")
    @JsonIgnoreProperties({"metadata", "description"})
    private RetentionPolicy retentionPolicy;

}