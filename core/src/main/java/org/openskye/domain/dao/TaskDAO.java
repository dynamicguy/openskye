package org.openskye.domain.dao;


import com.google.common.base.Optional;
import org.openskye.domain.Task;
import org.openskye.domain.TaskSchedule;
import org.openskye.domain.TaskStatus;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * DAO for the {@link org.openskye.domain.Task}
 */
public class TaskDAO extends AbstractPaginatingDAO<Task> {

    public Optional<Task> findOldestQueued(String workerName) {
        Task nextTask = null;
        try {
            CriteriaBuilder builder = createCriteriaBuilder();
            CriteriaQuery<Task> criteria = builder.createQuery(Task.class);
            Root<Task> taskRoot = criteria.from(Task.class);
            criteria.select(taskRoot);
            criteria.where(builder.and(
                    builder.equal(taskRoot.get("status"), TaskStatus.QUEUED)),
                    builder.equal(taskRoot.get("workerName"), workerName)
            ).orderBy(builder.asc(taskRoot.get("queued")));
            List<Task> taskList = currentEntityManager().createQuery(criteria).getResultList();
            if (taskList.size() > 0) {
                nextTask = taskList.get(0);
            }
        } catch (NoResultException nre) {
            // There are no tasks in the database
        }
        if (nextTask == null) {
            return Optional.absent();
        } else {
            return Optional.of(nextTask);
        }
    }

    public PaginatedResult<Task> findLiveTasks() {

        CriteriaBuilder builder = createCriteriaBuilder();
        CriteriaQuery<Task> criteria = builder.createQuery(Task.class);
        Root<Task> taskRoot = criteria.from(Task.class);
        criteria.select(taskRoot);
        criteria.where(builder.equal(taskRoot.get("status"), TaskStatus.STARTED));
        PaginatedResult<Task> result = new PaginatedResult<>(currentEntityManager().createQuery(criteria).getResultList());
        return result;
    }

    // Create a Task from a TaskSchedule
    public Task create(TaskSchedule taskSchedule) {
        Task task = new Task();
        task.setProject(taskSchedule.getProject());
        task.setStepClassName(taskSchedule.getStepClassName());
        task.setStepJson(taskSchedule.getStepJson());
        return super.create(task);
    }

}
