package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.openskye.task.step.AbstractTaskStep;

import javax.persistence.*;

/**
 * A schedule to enqueue a task at a future time or periodically
 */
@Entity
@Table(name = "TASK_SCHEDULE")
@Data
@EqualsAndHashCode(of = "id")
@ToString(of = "id")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskSchedule implements Identifiable {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true, length = 36)
    private String id;
    @ManyToOne
    @JoinColumn(name = "PROJECT_ID")
    private Project project;

    // The details of the Task are contained in a subclass of AbstractTaskStep.  Since
    // each type of step has its own set of fields, store it as a JSON blob and
    // reconstruct the step object in the DAO when accessed
    @Column(name = "STEP_CLASS_NAME")
    private String stepClassName;
    @Lob @Basic(fetch=FetchType.EAGER)
    @Column(name = "STEP_JSON")
    @JsonIgnore
    private String stepJson;
    @Transient
    private AbstractTaskStep step;

    // See Quartz cron field documentation for formatting of this string
    @Column(name = "CRON_EXPRESSION")
    private String cronExpression;
}

