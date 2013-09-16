package org.skye.core;

import lombok.Data;

/**
 * A block of content that relates to a {@link SimpleObject}
 */
@Data
public class ArchiveContentBlock {

    private String id;
    private ArchiveStore archiveStore;

}
