package org.openskye.task.simple;

/**
 * A simple representation of the step for a task
 */
public interface TaskStep {

    void validate();

    void start();
}
