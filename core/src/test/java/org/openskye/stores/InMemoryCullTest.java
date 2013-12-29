package org.openskye.stores;

import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.PersistService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.ObjectSet;
import org.openskye.domain.*;
import org.openskye.domain.dao.NodeDAO;
import org.openskye.domain.dao.ProjectDAO;
import org.openskye.domain.dao.RetentionPolicyDAO;
import org.openskye.guice.InMemoryTestModule;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.task.TaskManager;
import org.openskye.task.step.CullTaskStep;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * A set of basic tests for the registry
 */
public class InMemoryCullTest {

    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(InMemoryTestModule.class);
    @Inject
    public TaskManager taskManager;
    @Inject
    private PersistService persistService;
    @Inject
    private ObjectMetadataRepository omr;
    @Inject
    private RetentionPolicyDAO retentionPolicyDAO;
    @Inject
    private ProjectDAO projectDAO;
    @Inject
    private NodeDAO nodeDAO;
    @Inject
    private Provider<EntityManager> emf;

    @Before
    public void checkStarted() {
        try {
            persistService.start();
        } catch (IllegalStateException e) {
            // Ignore it we are started
        }
    }

    private Project mockProject() {
        Project project = new Project();
        project.setName("Cull Test Project");
        return projectDAO.create(project);
    }

    private Node mockNode() {
        Node node = new Node();
        return nodeDAO.create(node);
    }

    private RetentionPolicy mockPolicy(String recordsCode, String name, long yearsRetained) {
        RetentionPolicy policy = new RetentionPolicy();
        policy.setRecordsCode(recordsCode);
        policy.setName(name);
        policy.setRetentionPeriod(yearsRetained);
        policy.setPeriodType(PeriodType.YEAR);
        return retentionPolicyDAO.create(policy);
    }

    private ObjectMetadata mockObject(Project project, int yearsOld, RetentionPolicy policy) {
        ObjectMetadata om = new ObjectMetadata();
        om.setId(UUID.randomUUID().toString());
        om.setProject(project);
        om.setLastModified(DateTime.now().minusYears(yearsOld));
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("recordsCode", policy.getRecordsCode());
        om.setMetadata(metadata);
        return omr.put(om);
    }

    @Test
    public void testBasicCulling() throws Exception {

        emf.get().getTransaction().begin();
        Project project = mockProject();
        Node node = mockNode();
        RetentionPolicy p3 = mockPolicy("CULL_P3", "Cull Test 3 Year Policy", 3);
        RetentionPolicy p5 = mockPolicy("CULL_P5", "Cull Test 5 Year Policy", 5);
        ObjectMetadata y1p3 = mockObject(project, 1, p3);
        ObjectMetadata y2p5 = mockObject(project, 2, p5);
        ObjectMetadata y4p3 = mockObject(project, 4, p3);
        ObjectMetadata y4p5 = mockObject(project, 4, p5);
        ObjectMetadata y6p5 = mockObject(project, 6, p5);
        emf.get().getTransaction().commit();

        CullTaskStep cullStep = new CullTaskStep(project, node);
        Task cull = cullStep.toTask();
        taskManager.submit(cull);

        ObjectSet cullSet = cullStep.getObjectSet();
        assertThat("Found culled object set", cullSet != null);
        assertThat("1 year old object with 3 year policy not culled", omr.isObjectInSet(cullSet, y1p3) == false);
        assertThat("2 year old object with 5 year policy not culled", omr.isObjectInSet(cullSet, y2p5) == false);
        assertThat("4 year old object with 3 year policy culled", omr.isObjectInSet(cullSet, y4p3) == true);
        assertThat("4 year old object with 5 year policy not culled", omr.isObjectInSet(cullSet, y4p5) == false);
        assertThat("6 year old object with 5 year policy culled", omr.isObjectInSet(cullSet, y6p5) == true);
    }

}
