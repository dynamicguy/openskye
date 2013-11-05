package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

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
    @Column(name = "TASK_TYPE")
    private TaskType taskType;
    @ManyToOne
    @JoinColumn(name = "PROJECT_ID")
    private Project project;
    // For a discovery/archive we will provide the channel
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CHANNEL_ID")
    private Channel channel;
    // When we are extracting we will provide the target information store definition
    @ManyToOne
    @JoinColumn(name = "TARGET_INFORMATION_STORE_DEFINITION_ID")
    private InformationStoreDefinition targetInformationStoreDefinition;
    // We need to be able to handle an ObjectSet,
    // if an ObjectSetId is provided we will only add upon
    // those objects,  this doesn't not apply to discover
    @Column(name = "OBJECT_SET_ID")
    private String objectSetId;
    // Other task parameters, currently only used for TEST tasks
    @Column(name = "TASK_PARAMETERS")
    private String taskParameters;

    // See Quartz cron field documentation for formatting of this string
    @Column(name = "CRON_EXPRESSION")
    private String cronExpression;
}

