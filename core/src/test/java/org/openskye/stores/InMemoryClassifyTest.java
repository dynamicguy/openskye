package org.openskye.stores;

import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.PersistService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openskye.core.ObjectMetadata;
import org.openskye.domain.*;
import org.openskye.domain.dao.NodeDAO;
import org.openskye.domain.dao.ProjectDAO;
import org.openskye.domain.dao.RetentionPolicyDAO;
import org.openskye.guice.InMemoryTestModule;
import org.openskye.metadata.ObjectMetadataRepository;
import org.openskye.metadata.ObjectMetadataSearch;
import org.openskye.task.TaskManager;
import org.openskye.task.step.ClassifyTaskStep;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * A set of basic tests for the registry
 */
public class InMemoryClassifyTest {

    @Rule
    public final GuiceBerryRule guiceBerry = new GuiceBerryRule(InMemoryTestModule.class);
    @Inject
    public TaskManager taskManager;
    @Inject
    private PersistService persistService;
    @Inject
    private ObjectMetadataRepository omr;
    @Inject
    private ObjectMetadataSearch oms;
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

    private RetentionPolicy mockPolicy(String recordsCode, String name, int priority, String criteria) {
        RetentionPolicy policy = new RetentionPolicy();
        policy.setRecordsCode(recordsCode);
        policy.setName(name);
        policy.setPriority(priority);
        policy.setMetadataCriteria(criteria);
        policy.setPeriodType(PeriodType.YEAR);
        return retentionPolicyDAO.create(policy);
    }

    private ObjectMetadata mockObject(Project project, String path, RetentionPolicy policy) {
        ObjectMetadata om = new ObjectMetadata();
        om.setId(UUID.randomUUID().toString());
        om.setProject(project);
        om.setPath(path);
        Map<String, String> metadata = new HashMap<>();
        if (policy != null) {
            metadata.put("recordsCode", policy.getRecordsCode());
        }
        om.setMetadata(metadata);
        ObjectMetadata omInserted = omr.put(om);
        oms.index(omInserted);
        return omInserted;
    }

    private String getRecordsCode(ObjectMetadata om) {
        return omr.get(om.getId()).get().getMetadata().get("recordsCode");
    }

    @Test
    public void testBasicClassification() throws Exception {

        emf.get().getTransaction().begin();
        Project project = mockProject();
        RetentionPolicy r0 = mockPolicy("CL_BASE", "Classify Test Base", 0, null);
        RetentionPolicy rA = mockPolicy("CL_PATH_A", "Classify Test Path A", 2, "*A*");
        RetentionPolicy rB = mockPolicy("CL_PATH_B", "Classify Test Path B", 2, "*B*");
        RetentionPolicy rC = mockPolicy("CL_PATH_C", "Classify Test Path C", 3, "*C*");
        RetentionPolicy rH = mockPolicy("CL_HI_PRI", "Classify Test High Priority", 4, null);
        ObjectMetadata pArNull = mockObject(project, "pA", null);
        ObjectMetadata p0r0 = mockObject(project, "p0", r0);
        ObjectMetadata pCr0 = mockObject(project, "pC", r0);
        ObjectMetadata pBCr0 = mockObject(project, "pBC", r0);
        ObjectMetadata pABr0 = mockObject(project, "pAB", r0);
        ObjectMetadata pBrA = mockObject(project, "pB", rA);
        ObjectMetadata pBrB = mockObject(project, "pB", rB);
        ObjectMetadata pCrH = mockObject(project, "pC", rH);
        emf.get().getTransaction().commit();

        int nIndexed = 0;
        Iterable<ObjectMetadata> hits = oms.search(project, "*");
        if (hits != null) {
            for (ObjectMetadata hit : hits) {
                nIndexed++;
            }
        }
        assertThat("Mock objects inserted and indexed", nIndexed, is(equalTo(8)));

        ClassifyTaskStep classifyStep = new ClassifyTaskStep(project, mockNode());
        Task classify = classifyStep.toTask();
        taskManager.submit(classify);
        assertThat("Classify task completed", classify.getStatus(), is(equalTo(TaskStatus.COMPLETED)));
        assertThat("Mock objects found", classify.getStatistics().getSimpleObjectsFound(), is(equalTo(7L)));
        assertThat("Eligible objects changed", classify.getStatistics().getSimpleObjectsProcessed(), is(equalTo(3L)));

        assertThat("Null policy replaced", getRecordsCode(pArNull), is(equalTo("CL_PATH_A")));
        assertThat("No effect if no criteria matched", getRecordsCode(p0r0), is(equalTo("CL_BASE")));
        assertThat("Low priority policy replaced", getRecordsCode(pCr0), is(equalTo("CL_PATH_C")));
        assertThat("Highest priority policy used", getRecordsCode(pBCr0), is(equalTo("CL_PATH_C")));
        assertThat("Tie between new policies means no change", getRecordsCode(pABr0), is(equalTo("CL_BASE")));
        assertThat("Tie with existing policy means no change", getRecordsCode(pBrA), is(equalTo("CL_PATH_A")));
        assertThat("Already has same policy means no change", getRecordsCode(pBrB), is(equalTo("CL_PATH_B")));
        assertThat("Existing high priority policy means no change", getRecordsCode(pCrH), is(equalTo("CL_HI_PRI")));
    }
}
