package org.openskye.domain.dao;

import org.openskye.domain.Task;
import org.openskye.domain.TaskLog;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * DAO for the {@link org.openskye.domain.TaskLog}
 */
public class TaskLogDAO extends AbstractPaginatingDAO<TaskLog> {

    public PaginatedResult<TaskLog> getLogsForTask(Task task) {
        CriteriaBuilder builder = getCriteriaBuilder();
        CriteriaQuery<TaskLog> criteria = builder.createQuery(TaskLog.class);
        Root<TaskLog> taskLogRoot = criteria.from(TaskLog.class);
        criteria.select(taskLogRoot);
        criteria.where(builder.equal(taskLogRoot.get("taskId"), task.getId()));
        List<TaskLog> resultList = getPaginatedQuery(criteria, taskLogRoot).getResultList();
        return new PaginatedResult<>(resultList);
    }
}
