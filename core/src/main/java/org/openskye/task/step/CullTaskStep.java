package org.openskye.task.step;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Inject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.ObjectSet;
import org.openskye.core.SkyeException;
import org.openskye.domain.Project;
import org.openskye.domain.RetentionPolicy;
import org.openskye.domain.TaskStatus;
import org.openskye.domain.dao.ProjectDAO;
import org.openskye.domain.dao.RetentionPolicyDAO;

import java.util.HashMap;
import java.util.Map;

/**
 * The Cull task identifies all objects in a project's archive store that are
 * eligible for destruction based on their retention date and groups them into
 * a new ObjectSet.
 */
@NoArgsConstructor
@Slf4j
public class CullTaskStep extends TaskStep {

    @Inject
    private ProjectDAO projectDAO;
    @Inject
    private RetentionPolicyDAO retentionPolicyDAO;
    @JsonProperty  // to override the JsonIgnore in the parent class
    @Getter
    @Setter
    private Project project;

    public CullTaskStep(Project project) {
        this.project = project;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public String getLabel() {
        return "CULL";
    }

    @Override
    public void rehydrate() {
        if ( project.getName() == null ) {
            project = projectDAO.get(project.getId()).get();
        }
    }

    @Override
    public void validate() {
        if (project == null) {
            throw new SkyeException("Task " + task.getId() + " is missing a project and so can not cull");
        }
    }

    @Override
    public TaskStatus call() throws Exception {

        // Load retention policy information up front, to avoid a database query on every object
        Map<String,RetentionPolicy> policyMap = new HashMap<String,RetentionPolicy>();
        for ( RetentionPolicy policy : retentionPolicyDAO.list().getResults() ) {
            policyMap.put(policy.getRecordsCode(),policy);
        }

        beginTransaction();

        String setName = project.getName() + " - retention expired " + new DateTime();
        ObjectSet set = omr.createObjectSet(setName);

        for ( ObjectMetadata om : omr.getObjects(project) ) {
            //TODO: ensure recordsCode field is attached to each object during ingestion
            String recordsCode = om.getMetadata().get("recordsCode");
            if ( recordsCode == null ) {
                // objects without a retention policy are not culled
            } else if ( ! policyMap.containsKey(recordsCode) ) {
                // objects with an unrecognized record code are not culled
            } else if ( isPastRetention( om ,policyMap.get(recordsCode) ) ) {
                omr.addObjectToSet( set, om );
            }
        }

        //TODO: exclude objects on hold

        commitTransaction();

        return TaskStatus.COMPLETED;
    }

    private boolean isPastRetention( ObjectMetadata om, RetentionPolicy policy ) {
        DateTime triggerDate = om.getLastModified(); //TODO: trigger date may be something else
        int retentionDays = 0;
        switch ( policy.getPeriodType() ) {
            case PERM:
                return false;
            case YEAR:
                retentionDays = (int) Math.ceil(policy.getRetentionPeriod() * 365.25);
                break;
            case MONTH:
                retentionDays = (int) Math.ceil(policy.getRetentionPeriod() * 30);
                break;
            case DAY:
                retentionDays = (int) (policy.getRetentionPeriod() * 1.0);
                break;
        }
        return DateTime.now().isAfter(triggerDate.plusDays(retentionDays));
    }

}