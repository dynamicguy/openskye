package org.openskye.domain;

/**
 * A status for a {@link org.openskye.domain.Task}
 */
public enum TaskStatus {
    QUEUED, STARTING, RUNNING, FAILED, COMPLETED, CANCELED
}
