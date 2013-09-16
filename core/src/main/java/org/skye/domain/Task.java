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
    @ManyToOne
    private Channel channel;
    // We need to be able to handle an ObjectSet
    private String objectSetId;

}
