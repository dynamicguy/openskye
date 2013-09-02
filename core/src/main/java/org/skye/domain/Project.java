package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A project
 */
@Entity
@Table(name = "PROJECT")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Project {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;
    @ManyToOne
    private Domain domain;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Channel> channels = new ArrayList<>();
    private String name;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Task> tasks = new ArrayList<>();

}
