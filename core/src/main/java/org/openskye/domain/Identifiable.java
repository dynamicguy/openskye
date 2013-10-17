package org.openskye.domain;

/**
 * A helper interface that is implemented by all our
 * domain objects since we know they all have to have an
 * id
 */
public interface Identifiable {
    public String getId();
}
