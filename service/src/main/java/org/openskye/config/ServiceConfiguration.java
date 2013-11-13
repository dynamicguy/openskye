package org.openskye.config;

import lombok.Data;
import org.openskye.metadata.impl.InMemoryObjectMetadataRepository;
import org.openskye.metadata.impl.InMemoryObjectMetadataSearch;
import org.openskye.task.simple.InMemoryTaskManager;
import org.openskye.task.simple.InMemoryTaskScheduler;

@Data
public class ServiceConfiguration {

    private String omr = InMemoryObjectMetadataRepository.class.getCanonicalName();
    private String oms = InMemoryObjectMetadataSearch.class.getCanonicalName();
    private String taskManager = InMemoryTaskManager.class.getCanonicalName();
    private String taskScheduler = InMemoryTaskScheduler.class.getCanonicalName();

}
