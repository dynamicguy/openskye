package org.skye.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The representation of a channel within Skye
 */
@Entity
@Table(name = "CHANNEL")
public class Channel {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;
    @ManyToOne
    private Project project;
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<ChannelArchiveStore> channelArchiveStores = new ArrayList<>();
    private String name;

    public List<ChannelArchiveStore> getChannelArchiveStores() {
        return channelArchiveStores;
    }

    public void setChannelArchiveStores(List<ChannelArchiveStore> channelArchiveStores) {
        this.channelArchiveStores = channelArchiveStores;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
