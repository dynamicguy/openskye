package org.skye.core;

import lombok.Data;

/**
 * A base abstract type to represent a simple object within the enterprise
 */
@Data
public abstract class SimpleObject {

    private ObjectMetadata objectMetadata = new ObjectMetadata();

}
