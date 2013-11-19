package org.openskye.domain.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openskye.core.SkyeException;
import org.openskye.domain.Task;
import org.openskye.domain.TaskLog;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.List;

/**
 * DAO for the {@link org.openskye.domain.TaskLog}
 */
public class TaskLogDAO extends AbstractPaginatingDAO<TaskLog> {

    private final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void deserialize(TaskLog taskLog) {
        if (taskLog.getExceptionJson() == null) {
            taskLog.setException(null);
        } else {
            try {
                Exception exception = (Exception) MAPPER.readValue(taskLog.getExceptionJson(), Exception.class);
                taskLog.setException(exception);
            } catch (ClassCastException | IOException e) {
                throw new SkyeException("Unable to deserialize exception in task log", e);
            }
        }
    }

    @Override
    protected void serialize(TaskLog taskLog) {
        if (taskLog.getException() == null) {
            taskLog.setExceptionJson(null);
        } else {
            try {
                taskLog.setExceptionJson(MAPPER.writeValueAsString(taskLog.getException()));
            } catch (IOException e) {
                throw new SkyeException("Unable to serialize exception in task log", e);
            }
        }
    }

    public PaginatedResult<TaskLog> getLogsForTask(Task task) {
        CriteriaBuilder builder = createCriteriaBuilder();
        CriteriaQuery<TaskLog> criteria = builder.createQuery(TaskLog.class);
        Root<TaskLog> taskLogRoot = criteria.from(TaskLog.class);
        criteria.select(taskLogRoot);
        criteria.where(builder.equal(taskLogRoot.get("taskId"), task.getId()));
        List<TaskLog> resultList = currentEntityManager().createQuery(criteria).getResultList();
        return new PaginatedResult<>(resultList);
    }
}
