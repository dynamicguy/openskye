package org.skye.task.simple;

import org.skye.core.*;
import org.skye.domain.AttributeInstance;
import org.skye.domain.ChannelFilterDefinition;
import org.skye.domain.Task;
import org.skye.filters.ChannelFilter;
import org.skye.filters.ChannelFilterFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple implementation of the discover task type
 */
public class ExtractTaskStep extends AbstractTaskStep {

    private final Task task;
    private List<ChannelFilter> filters = new ArrayList<>();

    public ExtractTaskStep(Task task) {
        this.task = task;
    }

    @Override
    public void validate() {
        if (task.getObjectSetId() == null) {
            throw new SkyeException("Task " + task.getId() + " is missing an object set id");
        }
        if (task.getTargetInformationStoreDefinition() == null) {
            throw new SkyeException("Task " + task.getId() + " is missing a target information store definition");
        }
    }

    @Override
    public void start() {



    }

}
