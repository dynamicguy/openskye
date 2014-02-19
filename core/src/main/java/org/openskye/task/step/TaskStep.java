package org.openskye.task.step;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openskye.core.*;
import org.openskye.domain.*;
import org.openskye.domain.dao.AuditLogDAO;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.metadata.ObjectMetadataSearch;
import org.openskye.stores.StoreRegistry;
import org.openskye.task.TaskManager;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * An abstract base for the {@link TaskStep}
 */
@Slf4j
public abstract class TaskStep implements Callable<TaskStatus> {

    @JsonIgnore
    @Inject
    protected StoreRegistry storeRegistry;
    @JsonIgnore
    @Inject
    protected ObjectMetadataRepository omr;
    @JsonIgnore
    @Inject
    protected ObjectMetadataSearch oms;
    @JsonIgnore
    @Inject
    protected TaskManager taskManager;
    @JsonIgnore
    protected boolean hasOuterTransaction = false;  // is this task already wrapped in an outer transaction?
    @JsonIgnore
    @Inject
    protected AuditLogDAO auditLogDAO;
    @JsonIgnore
    @Getter
    @Setter
    Task task;
    @Getter
    @Setter
    private Node node;
    @JsonIgnore
    @Inject
    private Provider<EntityManager> emf;

    public abstract void validate();

    @JsonIgnore
    protected abstract Project getStepProject();

    @JsonIgnore
    public Project getProject() {
        rehydrate();
        return getStepProject();
    }

    protected void beginTransaction() {
        hasOuterTransaction = emf.get().getTransaction().isActive();
        if (!hasOuterTransaction) {
            emf.get().getTransaction().begin();
        }
    }

    protected void commitTransaction() {
        if (!hasOuterTransaction && !emf.get().getTransaction().getRollbackOnly()) {
            emf.get().getTransaction().commit();
        }
    }

    public Task toTask() {
        // Create a new Task object from this step
        task = new Task();
        task.setProject(getProject());
        task.setAssignedNode(getNode());
        task.setStep(this);
        task.setStepClassName(this.getClass().getName());
        task.setStepLabel(this.getLabel());
        task.setStatus(TaskStatus.CREATED);
        task.setStatistics(new TaskStatistics());
        return task;
    }

    public TaskSchedule toTaskSchedule(String cronExpression) {
        // Create a new TaskSchedule object from this step
        TaskSchedule taskSchedule = new TaskSchedule();
        taskSchedule.setProject(getProject());
        taskSchedule.setStepClassName(this.getClass().getName());
        taskSchedule.setStep(this);
        taskSchedule.setCronExpression(cronExpression);
        return taskSchedule;
    }

    protected InformationStore buildInformationStore(InformationStoreDefinition dis) {
        Optional<InformationStore> is = storeRegistry.build(dis);
        if (!is.isPresent())
            throw new SkyeException("Unable to build information store");
        return is.get();
    }

    protected ArchiveStore buildArchiveStore(ArchiveStoreInstance archiveStoreInstance) {
        Optional<ArchiveStore> as = storeRegistry.build(archiveStoreInstance);
        if (!as.isPresent())
            throw new SkyeException("Unable to build archive store");
        return as.get();
    }

    protected void auditObject(SimpleObject simpleObject, ObjectEvent e) {
        log.debug("Auditing change to " + simpleObject);
        AuditLog auditLog = new AuditLog();
        auditLog.setAuditEntity(simpleObject.getClass().getSimpleName());
        auditLog.setAuditEvent(AuditEvent.OBJECT);
        auditLog.setObjectEvent(e);
        auditLog.setObjectAffected(simpleObject.getObjectMetadata().getId());
        auditLog.setUser(auditLogDAO.getCurrentUser());
        auditLogDAO.create(auditLog);
    }

    protected void auditObject(ObjectMetadata om, ObjectEvent e) {
        log.debug("Auditing change to " + om);
        AuditLog auditLog = new AuditLog();
        auditLog.setAuditEntity(ObjectMetadata.class.getSimpleName());
        auditLog.setAuditEvent(AuditEvent.OBJECT);
        auditLog.setObjectEvent(e);
        auditLog.setObjectAffected(om.getId());
        auditLog.setUser(auditLogDAO.getCurrentUser());
        auditLogDAO.create(auditLog);
    }

    protected Optional<ObjectEvent> getLatestEvent(ObjectMetadata objectMetadata){
        if(auditLogDAO.findByObject(objectMetadata.getId()).isPresent()){
            List<AuditLog> objectLogs = auditLogDAO.findByObject(objectMetadata.getId()).get();
            return Optional.of(objectLogs.get(objectLogs.size()-1).getObjectEvent());
        } else {
            return Optional.absent();
        }
    }

    @Override
    public TaskStatus call() throws Exception {
        TaskStatus status = TaskStatus.COMPLETED;
        SkyeException exception = null;

        log.debug("Starting transaction for task " + getTask().getId());
        beginTransaction();

        try {
            log.debug("Performing task step for " + getTask().getId());
            status = doStep();
        } catch (Exception ex) {
            status = TaskStatus.FAILED;
            log.warn("Task " + getTask().getId() + " failed due to exception of type " + ex.getClass().getCanonicalName());
            exception = new SkyeException("Transaction Step failed.", ex);
            emf.get().getTransaction().setRollbackOnly();
        } finally {
            commitTransaction();
            log.debug("Committing transaction for task " + getTask().getId());
        }

        if (exception != null)
            throw exception;

        return status;
    }

    protected void toLog(String message, Exception e) {
        taskManager.toLog(task, message, e);
    }

    protected void toLog(String message) {
        taskManager.toLog(task, message);
    }

    protected abstract TaskStatus doStep() throws Exception;

    @JsonIgnore
    public abstract String getLabel();  // example: "ARCHIVE"

    /**
     * When a task step is pulled back from JSON it loses the JPA relationships
     * so we need to make sure we can pull them back in place
     */
    public abstract void rehydrate();
}
