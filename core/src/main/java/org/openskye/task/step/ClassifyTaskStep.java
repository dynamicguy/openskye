package org.openskye.task.step;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.SkyeException;
import org.openskye.domain.Node;
import org.openskye.domain.Project;
import org.openskye.domain.RetentionPolicy;
import org.openskye.domain.TaskStatus;
import org.openskye.domain.dao.ProjectDAO;
import org.openskye.domain.dao.RetentionPolicyDAO;

import java.util.*;

/**
 * The Classify task identifies all objects in a project's archive store that match
 * the metadata criteria of one or more retention policies.  Those objects are reclassified
 * with the records code of the highest priority policy they match.  If there is a tie
 * in priority, the object is not reclassified.
 */
@NoArgsConstructor
@Slf4j
public class ClassifyTaskStep extends TaskStep {

    private Node node;
    @Inject
    private ProjectDAO projectDAO;
    @Inject
    private RetentionPolicyDAO retentionPolicyDAO;
    @JsonProperty  // to override the JsonIgnore in the parent class
    @Getter
    @Setter
    private Project project;

    public ClassifyTaskStep(Project project, Node node) {
        this.project = project;
        this.node = node;
    }

    @Override
    public Project getStepProject() {
        return project;
    }

    @Override
    public String getLabel() {
        return "CLASSIFY";
    }

    @Override
    public void rehydrate() {
        if (project.getName() == null) {
            project = projectDAO.get(project.getId()).get();
        }
    }

    @Override
    public void validate() {
        if (project == null) {
            throw new SkyeException("Task " + task.getId() + " is missing a project and so can not classify");
        }
    }

    @Override
    protected Node getNode() {
        return node;
    }

    @Override
    public TaskStatus call() throws Exception {

        // Load retention policy information up front, to avoid a database query on every object
        Map<String, RetentionPolicy> policyMap = new HashMap<>();
        for (RetentionPolicy policy : retentionPolicyDAO.list().getResults()) {
            policyMap.put(policy.getRecordsCode(), policy);
        }

        // Build a map from object IDs to the set of policies with criteria that the object matches
        Map<String, List<RetentionPolicy>> criteriaMatch = new HashMap<>();
        for (RetentionPolicy policy : retentionPolicyDAO.list().getResults()) {
            Iterable<ObjectMetadata> hits = null;
            if (policy.getMetadataCriteria() != null) {
                hits = oms.search(project, policy.getMetadataCriteria());
            }
            if (hits != null) {
                for (ObjectMetadata om : hits) {
                    List<RetentionPolicy> policyList;
                    if (criteriaMatch.containsKey(om.getId())) {
                        policyList = criteriaMatch.get(om.getId());
                    } else {
                        policyList = new ArrayList<>();
                        criteriaMatch.put(om.getId(), policyList);
                    }
                    policyList.add(policy);
                }
            }
        }

        // For each object, select the new retention policy
        long objectsFound = 0;
        long objectsProcessed = 0;
        Map<String, String> classifyMap = new HashMap<>();
        Comparator<RetentionPolicy> descendingPriority = new Comparator<RetentionPolicy>() {
            public int compare(RetentionPolicy policy1, RetentionPolicy policy2) {
                return policy2.getPriority() - policy1.getPriority();
            }
        };
        for (String objectId : criteriaMatch.keySet()) {
            objectsFound++;
            List<RetentionPolicy> matchingPolicies = criteriaMatch.get(objectId);
            RetentionPolicy currentPolicy = policyMap.get(omr.get(objectId).get().getMetadata().get("recordsCode"));
            Collections.sort(matchingPolicies, descendingPriority);
            if (matchingPolicies.size() == 0) {
                // no matching policies
            } else if (currentPolicy != null && matchingPolicies.get(0).getPriority() <= currentPolicy.getPriority()) {
                // the object's current retention policy is higher or equal priority, so don't reclassify
            } else if (matchingPolicies.size() > 1 &&
                    (matchingPolicies.get(0).getPriority() == matchingPolicies.get(1).getPriority())) {
                // tie for highest priority, so don't reclassify the object
            } else {
                // reclassify to the highest priority policy
                classifyMap.put(objectId, matchingPolicies.get(0).getRecordsCode());
                objectsProcessed++;
            }
        }

        // update the objects in the OMR
        beginTransaction();
        for (String objectId : classifyMap.keySet()) {
            ObjectMetadata om = omr.get(objectId).get();
            om.getMetadata().put("recordsCode", classifyMap.get(objectId));
            omr.put(om);
        }
        task.getStatistics().addSimpleObjectsFound(objectsFound);
        task.getStatistics().addSimpleObjectsProcessed(objectsProcessed);
        commitTransaction();

        return TaskStatus.COMPLETED;
    }

}