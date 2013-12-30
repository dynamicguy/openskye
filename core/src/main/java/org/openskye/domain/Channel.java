package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * The representation of a channel within Skye
 */
@Entity
@Table(name = "CHANNEL")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString(exclude = {"channelArchiveStores", "attributeInstances", "channelFilters"})
public class Channel implements Identifiable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PROJECT_ID")
    private Project project;
    @ManyToOne(fetch = FetchType.EAGER)
    private InformationStoreDefinition informationStoreDefinition;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "channel")
    @JsonManagedReference("channelArchiveStores")
    private List<ChannelArchiveStore> channelArchiveStores = new ArrayList<>();
    @NotNull
    @NotBlank
    private String name;
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "channel")
    private List<AttributeInstance> attributeInstances = new ArrayList<>();
    @JsonManagedReference("channelFilters")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "channel")
    private List<ChannelFilterDefinition> channelFilters = new ArrayList<>();

}