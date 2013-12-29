package org.openskye.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Embeddable;
import java.util.UUID;

/**
 * A block of content that relates to one or more {@link SimpleObject}
 * and has the internal identifier for the storage of that block of
 * content within the archive store
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Embeddable
@EqualsAndHashCode(of = "id")
public class ArchiveContentBlock {

    private String id = UUID.randomUUID().toString();
    private String archiveStoreInstanceId;
    private String nodeId;
    private String checksum;
    private long originalSize;
    private long compressedSize;

}
