package org.openskye.domain;

/**
 * A status for a {@link org.openskye.domain.Task}
 */
public enum TaskStatus {
    CREATED,      // job's initial status, it will not be started yet
    QUEUED,       // the job is waiting for a worker thread to accept and run it
    STARTED,      // the job has been started by a worker thread
    FAILED,       // the job threw an exception while running
    COMPLETED,    // the job ran to completion without exceptions
    ABORTED       // the job was externally halted while running
}
