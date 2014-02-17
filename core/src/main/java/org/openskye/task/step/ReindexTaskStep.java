package org.openskye.task.step;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openskye.core.AggregateException;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.SkyeException;
import org.openskye.domain.Node;
import org.openskye.domain.Project;
import org.openskye.domain.TaskStatistics;
import org.openskye.domain.TaskStatus;

import org.openskye.domain.dao.NodeDAO;
import org.openskye.domain.dao.ProjectDAO;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.metadata.ObjectMetadataSearch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This {@link TaskStep} is intended to recover from some issue with search indexing.
 * First, it removes existing search index information, which may be incomplete.
 * Then, it creates new, up to date index information for objects in the {@link ObjectMetadataRepository}.
 * <p/>
 * Currently, this operation may optionally specify a particular {@link Project} to be Reindexed.
 * If no {@link Project} is specified, then all the operation is run for each {@link Project}.
 */
@NoArgsConstructor
@Slf4j
public class ReindexTaskStep extends TaskStep {
    @JsonProperty
    @Setter
    private Project project = null;

    @Inject
    @JsonIgnore
    private ProjectDAO projectDAO;

    @Inject
    @JsonIgnore
    private NodeDAO nodeDAO;

    @Inject
    @JsonIgnore
    private ObjectMetadataRepository omr;

    @Inject
    @JsonIgnore
    private ObjectMetadataSearch oms;

    @JsonIgnore
    private static final String STEP_LABEL = "REINDEX";

    ReindexTaskStep(Node node) {
        setNode(node);
    }

    ReindexTaskStep(Node node, Project project) {
        setNode(node);
        this.project = project;
    }


    @Override
    public void validate() {
    }

    @Override
    protected Project getStepProject() {
        return project;
    }

    @Override
    protected TaskStatus doStep() throws Exception {
        // TODO Add auditing.
        log.debug("Starting reindex task" + task);
        if (task.getStatistics() == null)
            task.setStatistics(new TaskStatistics());

        Iterable<ObjectMetadata> omResultSet;

        if (project == null) {
            log.debug("Clear all search indices.");
            oms.clear();

            log.debug("Getting all ObjectMetadata to index.");
            omResultSet = omr.getAllObjects();
        } else {
            log.debug("Delete search indices for project " + project);
            oms.delete(project);

            log.debug("Getting ObjectMetadata to index for project" + project);
            omResultSet = omr.getObjects(project);
        }

        try {
            oms.index(omResultSet);
        } catch (AggregateException ex) {
            toLog("exception while indexing ", ex.getFirstException());
            toLog(ex.count() + " objects failed to index.");
        } catch (Exception ex) {
            toLog("exception while indexing ", ex);
        }

        return TaskStatus.COMPLETED;
    }

    @Override
    public String getLabel() {
        return STEP_LABEL;
    }

    @Override
    public void rehydrate() {
        if (project != null) {
            Optional<Project> projectResult = projectDAO.get(project.getId());

            if (!projectResult.isPresent())
                throw new SkyeException("Project ID is not found.");

            project = projectResult.get();
        }

        Optional<Node> nodeResult = nodeDAO.get(getNode().getId());

        if (!nodeResult.isPresent())
            throw new SkyeException("Node ID is not found.");

        setNode(nodeResult.get());
    }
}
