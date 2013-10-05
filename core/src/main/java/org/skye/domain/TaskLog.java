package org.skye.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

/**
 * A log entry for a {@link Task}
 */
@Entity
@Table(name = "TASK_LOG")
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskLog implements Identifiable {
    @Id
    @GeneratedValue(generator = "uuid")
    @Column(unique = true)
    private String id;
    private Task task;
    private String message;

}
