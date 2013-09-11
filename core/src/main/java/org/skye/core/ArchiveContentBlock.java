package org.skye.core;

import lombok.Data;

/**
 * A block of content that relates to a {@link SimpleObject}
 */
@Data
public class ArchiveContentBlock {

    private String id;
    private ArchiveStore archiveStore;
    private long originalSize = 0;
    private long archiveSize = 0;
    private String mimeType;
    private String checksum;

}
