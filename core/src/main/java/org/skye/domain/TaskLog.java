package org.skye.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A log entry for a {@link Task}
 */
@Entity
@Table(name = "TASK_LOG")
public class TaskLog {

    private Task task;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
