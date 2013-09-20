package org.skye.core;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * An object set is a collection of {@link SimpleObject} that are grouped together
 * for a purpose
 */
@Data
public class ObjectSet {

    private String id;
    private String name;
}
