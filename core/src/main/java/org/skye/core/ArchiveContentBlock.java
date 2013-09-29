package org.skye.core;

import lombok.Data;

import java.util.UUID;

/**
 * A block of content that relates to a {@link SimpleObject}
 */
@Data
public class ArchiveContentBlock {

    private String id = UUID.randomUUID().toString();
    private String archiveStoreDefinitionId;

}
