package org.skye.task.inmemory;

/**
 * A simple representation of the step for a task
 */
public interface TaskStep {

    void validate();

    void start();
}
