package org.openskye.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * A simple representation of a page
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Page {

    private long pageNumber;
    private long pageSize;

}
