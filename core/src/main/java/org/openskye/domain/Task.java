package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.ser.LocalDateTimeSerializer;
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
    @JsonDeserialize(using=LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime queued;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @Column(name = "STARTED")
    @JsonDeserialize(using=LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime started;
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @Column(name = "ENDED")
    @JsonDeserialize(using=LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
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
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "stepClassName")
    @JsonSubTypes({
        @JsonSubTypes.Type(value=org.openskye.task.step.ArchiveTaskStep.class, name="ArchiveTaskStep"),
        @JsonSubTypes.Type(value=org.openskye.task.step.ClassifyTaskStep.class, name="ClassifyTaskStep"),
        @JsonSubTypes.Type(value=org.openskye.task.step.CullTaskStep.class, name="CullTaskStep"),
        @JsonSubTypes.Type(value=org.openskye.task.step.DestroyTaskStep.class, name="DestroyTaskStep"),
        @JsonSubTypes.Type(value=org.openskye.task.step.DiscoverTaskStep.class, name="DiscoverTaskStep"),
        @JsonSubTypes.Type(value=org.openskye.task.step.ExtractTaskStep.class, name="ExtractTaskStep"),
        @JsonSubTypes.Type(value=org.openskye.task.step.TestTaskStep.class, name="TestTaskStep"),
        @JsonSubTypes.Type(value=org.openskye.task.step.VerifyTaskStep.class, name="VerifyTaskStep"),
        @JsonSubTypes.Type(value=org.openskye.task.step.ReplicateTaskStep.class, name="ReplicateTaskStep"),
        @JsonSubTypes.Type(value=org.openskye.task.step.ReindexTaskStep.class, name="ReindexTaskStep")
    })
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
