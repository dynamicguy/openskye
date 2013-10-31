package org.openskye.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * An object set is a collection of {@link SimpleObject} that are grouped together
 * for a purpose
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObjectSet {

    private String id;
    private String name;
}
