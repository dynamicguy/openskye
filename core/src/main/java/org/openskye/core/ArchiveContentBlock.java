package org.openskye.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.UUID;

/**
 * A block of content that relates to a {@link SimpleObject}
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArchiveContentBlock {

    private String id = UUID.randomUUID().toString();
    private String archiveStoreDefinitionId;

}
