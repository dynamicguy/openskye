package org.skye.domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * A log entry for a {@link Task}
 */
@Entity
@Table(name = "TASK_LOG")
public class TaskLog {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(unique = true)
    protected String id;
    private Task task;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
