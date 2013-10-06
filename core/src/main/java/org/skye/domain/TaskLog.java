package org.skye.domain;

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
    @Column(unique = true)
    private String id;
    private Task task;
    private String message;

}
