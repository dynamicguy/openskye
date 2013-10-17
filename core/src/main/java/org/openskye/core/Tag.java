package org.openskye.core;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents a named tag that can be associated with {@link SimpleObject}
 */
@Data
@EqualsAndHashCode(of = "name")
public class Tag {

    private String name;

}
