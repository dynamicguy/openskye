package org.openskye.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.openskye.domain.Identifiable;

/**
 * An object set is a collection of {@link SimpleObject} that are grouped together
 * for a purpose
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ObjectSet implements Identifiable {

    private String id;
    private String name;
}
