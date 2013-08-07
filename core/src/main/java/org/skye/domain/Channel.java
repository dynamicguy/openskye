package org.skye.domain;

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

}
