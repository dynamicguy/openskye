package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * A Task
 */
@Entity
@Table(name = "TASK")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Task {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;
    private TaskType taskType;
    @ManyToOne
    private Task parentTask;
    @ManyToOne
    private Project project;
    @OneToOne
    private TaskStatistics statistics = new TaskStatistics();
    // For a discovery/archive we will provide the channel
    @ManyToOne
    private Channel channel;
    // When we are extracting we will provide the target information store definition
    @ManyToOne
    private InformationStoreDefinition targetInformationStoreDefinition;
    // We need to be able to handle an ObjectSet,
    // if an ObjectSetId is provided we will only add upon
    // those objects,  this doesn't not apply to discover
    private String objectSetId;

}
