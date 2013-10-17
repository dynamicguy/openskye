package org.openskye.core;

import lombok.Data;

/**
 * An object set is a collection of {@link SimpleObject} that are grouped together
 * for a purpose
 */
@Data
public class ObjectSet {

    private String id;
    private String name;
}