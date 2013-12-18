package org.openskye.task.step;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import lombok.Getter;
import lombok.Setter;
import org.openskye.core.ArchiveStore;
import org.openskye.core.InformationStore;
import org.openskye.core.SkyeException;
import org.openskye.domain.*;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.metadata.ObjectMetadataSearch;
import org.openskye.stores.StoreRegistry;

import javax.persistence.EntityManager;
import java.util.concurrent.Callable;

/**
 * An abstract base for the {@link TaskStep}
 */
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
    @Getter
    @Setter
    Task task;
    @JsonIgnore
    @Inject
    private Provider<EntityManager> emf;
    @JsonIgnore
    protected boolean hasOuterTransaction = false;  // is this task already wrapped in an outer transaction?

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
        if ( !hasOuterTransaction ) {
            emf.get().getTransaction().begin();
        }
    }

    protected void commitTransaction() {
        if ( !hasOuterTransaction ) {
            emf.get().getTransaction().commit();
        }
    }

    public Task toTask() {
        // Create a new Task object from this step
        task = new Task();
        task.setProject(getProject());
        //TODO: use default worker name for now
        task.setWorkerName("Skye Worker");
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
        return taskSchedule;
    }

    protected InformationStore buildInformationStore(InformationStoreDefinition dis) {
        Optional<InformationStore> is = storeRegistry.build(dis);
        if (!is.isPresent())
            throw new SkyeException("Unable to build information store");
        return is.get();
    }

    protected ArchiveStore buildArchiveStore(ArchiveStoreDefinition archiveStoreDefinition) {
        Optional<ArchiveStore> as = storeRegistry.build(archiveStoreDefinition);
        if (!as.isPresent())
            throw new SkyeException("Unable to build archive store");
        return as.get();
    }

    @JsonIgnore
    public abstract String getLabel();  // example: "ARCHIVE"

    /**
     * When a task step is pulled back from JSON it loses the JPA relationships
     * so we need to make sure we can pull them back in place
     */
    public abstract void rehydrate();
}
