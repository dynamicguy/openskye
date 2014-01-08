package org.openskye.domain;

/**
 * The events that are audited.
 */
public enum AuditEvent {
    /**
     * An event that directly affects a {@link org.openskye.core.SimpleObject}
     */
    OBJECT,
    /**
     * A new instance of a Skye component was inserted into the database.
     */
    INSERT,
    /**
     * A Skye component was updated.
     */
    UPDATE,
    /**
     * A Skye component was deleted.
     */
    DELETE,
    /**
     * A user logged into the system
     */
    LOGIN,
    /**
     * A user logged out of the system.
     */
    LOGOUT

}
