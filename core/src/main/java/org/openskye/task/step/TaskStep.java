package org.openskye.task.step;

import org.openskye.domain.TaskStatus;

import java.util.concurrent.Callable;

/**
 * A simple representation of the step for a task
 */
public interface TaskStep extends Callable<TaskStatus> {

    void validate();

}
