package org.openskye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.eclipse.persistence.annotations.UuidGenerator;

import javax.persistence.*;

/**
 * A log entry for a {@link Task}
 */
@Entity
@Table(name = "TASK_LOG")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@UuidGenerator(name = "TaskLogGenerator")
public class TaskLog implements Identifiable {
    @Id
    @GeneratedValue(generator = "TaskLogGenerator")
    @Column(unique = true, length = 36)
    private String id;
    @ManyToOne
    @JoinColumn(name = "TASK_ID")
    private Task task;
    private String message;

}
