package org.skye.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * A log entry for a {@link Task}
 */
@Entity
@Table(name = "TASK_LOG")
@Data
public class TaskLog {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;
    private Task task;

}
