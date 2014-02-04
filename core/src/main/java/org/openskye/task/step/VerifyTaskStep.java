package org.openskye.task.step;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openskye.core.ArchiveContentBlock;
import org.openskye.core.ArchiveStore;
import org.openskye.core.SkyeException;
import org.openskye.domain.*;
import org.openskye.domain.dao.ArchiveStoreDefinitionDAO;

/**
 * Implementation of a verification of a set of {@link org.openskye.core.SimpleObject}
 */
@NoArgsConstructor
@Slf4j
public class VerifyTaskStep extends TaskStep {
    @Inject
    private ArchiveStoreDefinitionDAO archiveStoreDefinitionDAO;

    @Getter
    @Setter
    private ArchiveStoreDefinition archiveStoreDefinition;

    public VerifyTaskStep(ArchiveStoreDefinition archiveStoreDefinition, Node node) {
        this.archiveStoreDefinition = archiveStoreDefinition;
        setNode(node);
    }

    @Override
    public String getLabel() {
        return "VERIFY";
    }

    @Override
    public void rehydrate() {
        if ( archiveStoreDefinition.getName() == null ) {
            archiveStoreDefinition = archiveStoreDefinitionDAO.get(archiveStoreDefinition.getId()).get();
        }
    }

    @Override
    public Project getStepProject() {
        return archiveStoreDefinition.getProject();
    }

    @Override
    public void validate() {
        if (archiveStoreDefinition == null) {
            throw new SkyeException("Task " + task.getId() + " is missing a target archive store definition");
        }
    }

    @Override
    protected TaskStatus doStep() throws Exception {
        int errors = 0;
        Project project = getProject();
        ArchiveStoreInstance asi = archiveStoreDefinition.getArchiveStoreInstance();
        ArchiveStore store = storeRegistry.build(asi).get();
        for ( ArchiveContentBlock acb : omr.getACBs(project, asi) ) {
            if ( ! store.verify(acb) ) {
                errors++;
            }
        }
        if ( errors == 0 ) {
            return TaskStatus.COMPLETED;
        } else {
            throw new SkyeException("Verification failed on "+errors+" ACBs");
        }
    }
}
