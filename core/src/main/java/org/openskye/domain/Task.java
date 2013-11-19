package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.openskye.task.step.TaskStep;

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
@JsonIgnoreProperties(ignoreUnknown=true)
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

    // The details of the Task are contained in a subclass of TaskStep.  Since
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
    private TaskStep step;
    @Transient
    private String stepLabel;

}
