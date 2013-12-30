package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;
import org.openskye.core.SkyeException;
import org.openskye.task.step.TaskStep;

import javax.persistence.*;
import java.io.IOException;

/**
 * A Task
 */
@Entity
@Table(name = "TASK")
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task implements Identifiable {

    @Transient
    private final static ObjectMapper MAPPER = new ObjectMapper();
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
    @ManyToOne
    @JoinColumn(name = "NODE_ID")
    private Node assignedNode;
    @Column(name = "STATUS")
    private TaskStatus status = TaskStatus.CREATED;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @Column(name = "QUEUED")
    private LocalDateTime queued;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @Column(name = "STARTED")
    private LocalDateTime started;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @Column(name = "ENDED")
    private LocalDateTime ended;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "task")
    private TaskStatistics statistics = new TaskStatistics();
    // The details of the Task are contained in a subclass of TaskStep.  Since
    // each type of step has its own set of fields, store it as a JSON blob and
    // reconstruct the step object in the DAO when accessed
    @Column(name = "STEP_CLASS_NAME")
    @JsonIgnore
    private String stepClassName;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "STEP_JSON",length=100000)
    @JsonIgnore
    private String stepJson;
    @Transient
    private TaskStep step;
    @Transient
    private String stepLabel;

    @PostLoad
    public void deserialize() {
        try {
            Class clazz = Class.forName(getStepClassName());
            TaskStep step = (TaskStep) MAPPER.readValue(getStepJson(), clazz);
            step.setTask(this);
            setStep(step);
            setStepLabel(step.getLabel());
        } catch (ReflectiveOperationException | IOException e) {
            throw new SkyeException("Unable to deserialize task step", e);
        }
    }

    @PrePersist
    @PreUpdate
    public void serialize() {
        try {
            setStepClassName(getStep().getClass().getName());
            setStepJson(MAPPER.writeValueAsString(getStep()));
        } catch (IOException e) {
            throw new SkyeException("Unable to serialize task step", e);
        }
    }

}
