package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The representation of a channel within Skye
 */
@Entity
@Table(name = "CHANNEL")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@UuidGenerator(name = "ChannelGenerator")
public class Channel implements Identifiable {

    @Id
    @GeneratedValue(generator = "ChannelGenerator")
    @Column(unique = true,length = 36)
    private String id;
    @ManyToOne
    private Project project;
    @ManyToOne
    private InformationStoreDefinition informationStoreDefinition;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<ChannelArchiveStore> channelArchiveStores = new ArrayList<>();
    private String name;
    @JsonIgnore
    @OneToMany
    private List<AttributeInstance> attributeInstances = new ArrayList<>();
    @JsonIgnore
    @OneToMany
    private List<ChannelFilterDefinition> channelFilters = new ArrayList<>();

}
