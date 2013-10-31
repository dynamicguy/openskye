package org.openskye.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Represents a named tag that can be associated with {@link SimpleObject}
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(of = "name")
public class Tag {

    private String name;

}
