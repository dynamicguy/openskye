package org.openskye.domain.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openskye.core.SkyeException;
import org.openskye.domain.TaskSchedule;
import org.openskye.task.step.TaskStep;

import java.io.IOException;

/**
 * DAO for the {@link org.openskye.domain.TaskSchedule}
 */
public class TaskScheduleDAO extends AbstractPaginatingDAO<TaskSchedule> {

    private final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void deserialize(TaskSchedule taskSchedule) {
        try {
            Class clazz = Class.forName(taskSchedule.getStepClassName());
            TaskStep step = (TaskStep) MAPPER.readValue(taskSchedule.getStepJson(),clazz);
            taskSchedule.setStep(step);
        } catch( ReflectiveOperationException|IOException e ) {
            throw new SkyeException("Unable to deserialize task schedule step",e);
        }
    }

    @Override
    protected void serialize(TaskSchedule taskSchedule) {
        try {
            taskSchedule.setStepClassName(taskSchedule.getStep().getClass().getName());
            taskSchedule.setStepJson(MAPPER.writeValueAsString(taskSchedule.getStep()));
        } catch( IOException e ) {
            throw new SkyeException("Unable to serialize task schedule step",e);
        }
    }
}
