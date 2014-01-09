package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.openskye.core.SkyeException;
import org.openskye.task.step.TaskStep;

import javax.persistence.*;
import java.io.IOException;

/**
 * A schedule to enqueue a task at a future time or periodically
 */
@Entity
@Table(name = "TASK_SCHEDULE")
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskSchedule implements Identifiable {
    @Transient
    private final static ObjectMapper MAPPER = new ObjectMapper();
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    @ManyToOne
    @JoinColumn(name = "PROJECT_ID")
    private Project project;
    // The details of the Task are contained in a subclass of TaskStep.  Since
    // each type of step has its own set of fields, store it as a JSON blob and
    // reconstruct the step object in the DAO when accessed
    @Column(name = "STEP_CLASS_NAME")
    private String stepClassName;
    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "STEP_JSON",length=100000)
    @JsonIgnore
    private String stepJson;
    @Transient
    private TaskStep step;
    // See Quartz cron field documentation for formatting of this string
    @Column(name = "CRON_EXPRESSION")
    private String cronExpression;

    @PostLoad
    public void deserialize() {
        try {
            Class clazz = Class.forName(getStepClassName());
            TaskStep step = (TaskStep) MAPPER.readValue(getStepJson(), clazz);
            setStep(step);
        } catch (ReflectiveOperationException | IOException e) {
            throw new SkyeException("Unable to deserialize task schedule step", e);
        }
    }

    @PreUpdate
    @PrePersist
    public void serialize() {
        try {
            setStepClassName(getStep().getClass().getName());
            setStepJson(MAPPER.writeValueAsString(getStep()));
        } catch (IOException e) {
            throw new SkyeException("Unable to serialize task schedule step", e);
        }
    }
}

