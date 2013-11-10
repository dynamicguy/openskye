package org.openskye.util;

import lombok.Data;
import org.openskye.config.WorkerConfiguration;
import java.util.List;

/**
 * Basic information about the worker, as displayed in the /workers REST endpoint
 */
@Data
public class WorkerInfo {
    WorkerConfiguration configuration;
    String[] taskIds;
}
