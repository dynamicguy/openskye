package org.openskye.domain.dao;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import org.openskye.core.SkyeException;
import org.openskye.domain.Task;
import org.openskye.domain.TaskSchedule;
import org.openskye.domain.TaskStatus;
import org.openskye.task.step.AbstractTaskStep;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.List;

/**
 * DAO for the {@link org.openskye.domain.Task}
 */
public class TaskDAO extends AbstractPaginatingDAO<Task> {

    private final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void deserialize(Task task) {
        try {
            Class clazz = Class.forName(task.getStepClassName());
            AbstractTaskStep step = (AbstractTaskStep) MAPPER.readValue(task.getStepJson(),clazz);
            step.setTask(task);
            task.setStep(step);
            task.setStepLabel(step.getLabel());
        } catch( ReflectiveOperationException|IOException e ) {
            throw new SkyeException("Unable to deserialize task step",e);
        }
    }

    @Override
    protected void serialize(Task task) {
        try {
            task.setStepClassName(task.getStep().getClass().getName());
            task.setStepJson(MAPPER.writeValueAsString(task.getStep()));
        } catch( IOException e ) {
            throw new SkyeException("Unable to serialize task step",e);
        }
    }

    public Optional<Task> findOldestQueued(String workerName) {
        CriteriaBuilder builder = createCriteriaBuilder();
        CriteriaQuery<Task> criteria = builder.createQuery(Task.class);
        Root<Task> taskRoot = criteria.from(Task.class);
        criteria.select(taskRoot);
        criteria.where(builder.and(
                builder.equal(taskRoot.get("status"), TaskStatus.QUEUED)),
                builder.equal(taskRoot.get("workerName"), workerName)
            ).orderBy(builder.asc(taskRoot.get("queued")));
        Task nextTask = currentEntityManager().createQuery(criteria).getSingleResult();
        if (nextTask == null) {
            return Optional.absent();
        } else {
            deserialize(nextTask);
            return Optional.of(nextTask);
        }
    }

    public PaginatedResult<Task> findLiveTasks() {
        PaginatedResult<Task> result = new PaginatedResult<>();
        CriteriaBuilder builder = createCriteriaBuilder();
        CriteriaQuery<Task> criteria = builder.createQuery(Task.class);
        Root<Task> taskRoot = criteria.from(Task.class);
        criteria.select(taskRoot);
        criteria.where(builder.equal(taskRoot.get("status"), TaskStatus.STARTED));
        List<Task> resultList = currentEntityManager().createQuery(criteria).getResultList();
        for ( Task task : resultList ) {
            deserialize(task);
        }
        result.setResults(resultList);
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
