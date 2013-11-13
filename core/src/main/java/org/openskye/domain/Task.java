package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.openskye.task.step.AbstractTaskStep;

import javax.persistence.*;
import java.util.Date;

/**
 * A Task
 */
@Entity
@Table(name = "TASK")
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Task implements Identifiable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    @ManyToOne
    @JoinColumn(name = "PARENT_TASK_ID")
    private Task parentTask;
    @ManyToOne
    @JoinColumn(name = "PROJECT_ID")
    private Project project;
    @Column(name = "WORKER_NAME")
    private String workerName;
    @Column(name = "STATUS")
    private TaskStatus status = TaskStatus.CREATED;
    @Temporal(TemporalType.DATE)
    @Column(name = "QUEUED")
    private Date queued;
    @Temporal(TemporalType.DATE)
    @Column(name = "STARTED")
    private Date started;
    @Temporal(TemporalType.DATE)
    @Column(name = "ENDED")
    private Date ended;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "task")
    private TaskStatistics statistics = new TaskStatistics();
<<<<<<< HEAD
    // For a discovery/archive we will provide the channel
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CHANNEL_ID")
    private Channel channel;
    // When we are extracting we will provide the target information store definition
    @ManyToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "TARGET_INFORMATION_STORE_DEFINITION_ID")
    private InformationStoreDefinition targetInformationStoreDefinition;
    // We need to be able to handle an ObjectSet,
    // if an ObjectSetId is provided we will only add upon
    // those objects,  this doesn't not apply to discover
    @Column(name = "OBJECT_SET_ID")
    private String objectSetId;
=======

    // The details of the Task are contained in a subclass of AbstractTaskStep.  Since
    // each type of step has its own set of fields, store it as a JSON blob and
    // reconstruct the step object in the DAO when accessed
    @Column(name = "STEP_CLASS_NAME")
    @JsonIgnore
    private String stepClassName;
    @Lob @Basic(fetch=FetchType.EAGER)
    @Column(name = "STEP_JSON")
    @JsonIgnore
    private String stepJson;
    @Transient
    private AbstractTaskStep step;
    @Transient
    private String stepLabel;
>>>>>>> task-manager-2

}
