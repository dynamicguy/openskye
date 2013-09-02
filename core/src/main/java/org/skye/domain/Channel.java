package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

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
public class Channel {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;
    @ManyToOne
    private Project project;
    @ManyToOne
    private DomainInformationStore domainInformationStore;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<ChannelArchiveStore> channelArchiveStores = new ArrayList<>();
    private String name;
    @JsonIgnore
    @OneToMany
    private List<AttributeInstance> attributeInstances = new ArrayList<>();

}
