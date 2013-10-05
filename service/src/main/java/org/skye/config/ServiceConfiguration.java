package org.skye.config;

import lombok.Data;
import org.skye.metadata.impl.InMemoryObjectMetadataRepository;
import org.skye.metadata.impl.InMemoryObjectMetadataSearch;
import org.skye.task.simple.InMemoryTaskManager;

@Data
public class ServiceConfiguration {

    private String omr = InMemoryObjectMetadataRepository.class.getCanonicalName();
    private String oms = InMemoryObjectMetadataSearch.class.getCanonicalName();
    private String taskManager = InMemoryTaskManager.class.getCanonicalName();

}
