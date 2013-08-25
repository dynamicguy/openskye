package org.skye.core;

import lombok.Data;
import org.joda.time.DateTime;

/**
 * A representation of a grouping of {@link SimpleObject}
 * <p/>
 * Typically a slice is created by a task when something is archived,  a {@link SimpleObject} will always belong
 * to a {@link Slice}
 */
@Data
public class Slice {

    private String id;
    private DateTime createdAt;

}
