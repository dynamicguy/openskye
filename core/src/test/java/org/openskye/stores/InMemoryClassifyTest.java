package org.openskye.stores;

import com.google.guiceberry.junit4.GuiceBerryRule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.persist.PersistService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.openskye.core.ObjectMetadata;
import org.openskye.domain.*;
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

    private RetentionPolicy mockPolicy(String recordsCode, String name, int priority, String criteria) {
        RetentionPolicy policy = new RetentionPolicy();
        policy.setRecordsCode(recordsCode);
        policy.setName(name);
        policy.setPriority(priority);
        policy.setMetadataCriteria(criteria);
        policy.setPeriodType(PeriodType.YEAR);
        return retentionPolicyDAO.create(policy);
    }

    private ObjectMetadata mockObject(Project project, String path, long originalSize, RetentionPolicy policy) {
        ObjectMetadata om = new ObjectMetadata();
        om.setId(UUID.randomUUID().toString());
        om.setProject(project);
        om.setPath(path);
        om.setOriginalSize(originalSize);
        Map<String,String> metadata = new HashMap<String,String>();
        if ( policy != null ) {
            metadata.put("recordsCode",policy.getRecordsCode());
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
    @Ignore
    public void testBasicClassification() throws Exception {
        final long KB = 1024;
        final long MB = KB*KB;

        emf.get().getTransaction().begin();
        Project project = mockProject();
        RetentionPolicy r0 = mockPolicy("CL_BASE", "Classify Test Base", 0, null);
        RetentionPolicy r1MB = mockPolicy("CL_SIZE_1MB","Classify Test Size 1 MB+",1,"originalSize:>="+1*MB);
        RetentionPolicy rA = mockPolicy("CL_PATH_A","Classify Test Path A",2,"path:*A");
        RetentionPolicy r1kB = mockPolicy("CL_SIZE_1kB","Classify Test Size 1 kB-",2,"originalSize:<"+1*KB);
        RetentionPolicy rMed = mockPolicy("CL_MED_PRI","Classify Test Medium Priority",2,null);
        RetentionPolicy rHi = mockPolicy("CL_HI_PRI","Classify Test High Priority",3,null);
        ObjectMetadata s12MrXpA = mockObject(project,"s12MrXpA",12*MB,null);
        ObjectMetadata s05KrXpA = mockObject(project,"s05KrXpA",5*KB,null);
        ObjectMetadata s07KrXpB = mockObject(project,"s07KrXpB",7*KB,null);
        ObjectMetadata s25MrXpB = mockObject(project,"s25MrXpB",25*MB,null);
        ObjectMetadata s10Mr0pA = mockObject(project,"s10Mr0pA",10*MB,r0);
        ObjectMetadata s16Kr0pA = mockObject(project,"s16Kr0pA",16*KB,r0);
        ObjectMetadata s128r0pA = mockObject(project,"s128r0pA",128,r0);
        ObjectMetadata s02MrMpA = mockObject(project,"s02MrMpA",2*MB,rMed);
        ObjectMetadata s02MrHpA = mockObject(project,"s02MrHpA",2*MB,rHi);
        emf.get().getTransaction().commit();

        int nIndexed = 0;
        Iterable<ObjectMetadata> hits = oms.search(project,"*");
        if ( hits != null ) {
            for ( ObjectMetadata hit : hits ) {
                nIndexed++;
            }
        }
        assertThat("Mock objects inserted and indexed", nIndexed, is(equalTo(9)));

        ClassifyTaskStep classifyStep = new ClassifyTaskStep(project);
        Task classify = classifyStep.toTask();
        taskManager.submit(classify);
        assertThat("Classify task completed", classify.getStatus(), is(equalTo(TaskStatus.COMPLETED)));
        assertThat("All mock objects found", classify.getStatistics().getSimpleObjectsFound(), is(equalTo(9L)));
        assertThat("Eligible objects changed", classify.getStatistics().getSimpleObjectsProcessed(), is(equalTo(5L)));

        assertThat("Higher priority policy takes precedence", getRecordsCode(s12MrXpA), is(equalTo("CL_PATH_A")));
        assertThat("Path based policy criteria used", getRecordsCode(s05KrXpA), is(equalTo("CL_PATH_A")));
        assertThat("No effect if no criteria matched", getRecordsCode(s07KrXpB), is(equalTo(null)));
        assertThat("Size based policy criteria used", getRecordsCode(s25MrXpB), is(equalTo("CL_SIZE_1MB")));
        assertThat("Low priority policy overridden", getRecordsCode(s10Mr0pA), is(equalTo("CL_PATH_A")));
        assertThat("Low priority policy overridden", getRecordsCode(s16Kr0pA), is(equalTo("CL_PATH_A")));
        assertThat("Tie between new policies means no change", getRecordsCode(s128r0pA), is(equalTo("CL_BASE")));
        assertThat("Tie with existing policy means no change", getRecordsCode(s02MrMpA), is(equalTo("CL_MED_PRI")));
        assertThat("Existing high priority policy means no change",getRecordsCode(s02MrHpA), is(equalTo("CL_HI_PRI")));
    }

}
