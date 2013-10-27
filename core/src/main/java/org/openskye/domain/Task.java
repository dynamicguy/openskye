package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;

/**
 * A Task
 */
@Entity
@Table(name = "TASK")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@UuidGenerator(name = "TaskGenerator")
@EqualsAndHashCode(of = "id")
public class Task implements Identifiable {
    @Id
    @GeneratedValue(generator = "TaskGenerator")
    @Column(unique = true, length = 36)
    private String id;
    @Column(name = "TASK_TYPE")
    private TaskType taskType;
    @ManyToOne
    @JoinColumn(name = "PARENT_TASK_ID")
    private Task parentTask;
    @ManyToOne
    @JoinColumn(name = "PROJECT_ID")
    private Project project;
    @OneToOne
    private TaskStatistics statistics = new TaskStatistics();
    // For a discovery/archive we will provide the channel
    @ManyToOne
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

}
