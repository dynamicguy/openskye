package org.openskye.domain.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openskye.core.SkyeException;
import org.openskye.domain.TaskLog;

import java.io.IOException;

/**
 * DAO for the {@link org.openskye.domain.TaskLog}
 */
public class TaskLogDAO extends AbstractPaginatingDAO<TaskLog> {

    private final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void deserialize(TaskLog taskLog) {
        if ( taskLog.getExceptionJson() == null ) {
            taskLog.setException(null);
        } else {
            try {
                Exception exception = (Exception) MAPPER.readValue(taskLog.getExceptionJson(),Exception.class);
                taskLog.setException(exception);
            } catch( ClassCastException|IOException e ) {
                throw new SkyeException("Unable to deserialize exception in task log",e);
            }
        }
    }

    @Override
    protected void serialize(TaskLog taskLog) {
        if ( taskLog.getException() == null ) {
            taskLog.setExceptionJson(null);
        } else {
            try {
                taskLog.setExceptionJson(MAPPER.writeValueAsString(taskLog.getException()));
            } catch( IOException e ) {
                throw new SkyeException("Unable to serialize exception in task log",e);
            }
        }
    }
}
