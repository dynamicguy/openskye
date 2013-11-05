package org.openskye.domain.dao;


import com.google.common.base.Optional;
import org.openskye.domain.Task;
import org.openskye.domain.TaskSchedule;
import org.openskye.domain.TaskStatus;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Predicate;

/**
 * DAO for the {@link org.openskye.domain.Task}
 */
public class TaskDAO extends AbstractPaginatingDAO<Task> {

    public Optional<Task> findOldestQueued() {
        CriteriaBuilder builder = createCriteriaBuilder();
        CriteriaQuery<Task> criteria = builder.createQuery(Task.class);
        Root<Task> taskRoot = criteria.from(Task.class);
        criteria.select(taskRoot);
        criteria.where(builder.equal(taskRoot.get("status"), TaskStatus.QUEUED))
                .orderBy(builder.asc(taskRoot.get("queued")));
        Task nextTask = currentEntityManager().createQuery(criteria).getSingleResult();
        if (nextTask == null) {
            return Optional.absent();
        } else {
            return Optional.of(nextTask);
        }
    }

    public PaginatedResult<Task> findLiveTasks() {
        PaginatedResult<Task> result = new PaginatedResult<>();
        CriteriaBuilder builder = createCriteriaBuilder();
        CriteriaQuery<Task> criteria = builder.createQuery(Task.class);
        Root<Task> taskRoot = criteria.from(Task.class);
        criteria.select(taskRoot);
        criteria.where(builder.or(
                builder.equal(taskRoot.get("status"), TaskStatus.STARTING),
                builder.equal(taskRoot.get("status"), TaskStatus.RUNNING))
        );
        result.setResults(currentEntityManager().createQuery(criteria).getResultList());
        return result;
    }

    // Create a Task from a TaskSchedule
    public Task create(TaskSchedule taskSchedule) {
        Task task = new Task();
        task.setProject(taskSchedule.getProject());
        task.setTaskType(taskSchedule.getTaskType());
        task.setObjectSetId(taskSchedule.getObjectSetId());
        task.setChannel(taskSchedule.getChannel());
        task.setTargetInformationStoreDefinition(taskSchedule.getTargetInformationStoreDefinition());
        return super.create(task);
    }

}
