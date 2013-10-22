package org.openskye.metadata.elasticsearch;

import com.google.guiceberry.junit4.GuiceBerryRule;
import org.elasticsearch.client.Client;
import org.junit.Rule;
import org.junit.Test;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.SkyeSession;
import org.openskye.domain.Domain;
import org.openskye.domain.Project;
import org.openskye.util.Page;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created with IntelliJ IDEA.
 * User: joshua
 * Date: 10/16/13
 * Time: 9:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchObjectMetadataSearchTest
{
    public static final String PROJECT_ID = "project1";
    public static final String METADATA_PATH = "/tmp/metadata";
    public static final int PAGE_NUMBER = 1;
    public static final int PAGE_SIZE = 50;

    @Rule
    public GuiceBerryRule guiceBerry = new GuiceBerryRule(InMemoryTestModule.class);

    @Inject
    public ElasticSearchObjectMetadataSearch search;

    @Inject
    public SkyeSession session;

    /**
     * This test sets up a single ObjectMetadata, indexes it, and attempts to
     * search for it using both search methods.
     */
    @Test
    public void indexAndSearchSimple()
    {
        // Create our objects for the test, and record the UUID's for some of
        // them. Since ObjectMetadata creates a random UUID, for example,
        // we need to capture it now.
        Project project = new Project();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        String metadataId = objectMetadata.getId();
        Page page = new Page();
        Iterable<ObjectMetadata> resultList;
        Iterator<ObjectMetadata> resultIterator;
        ObjectMetadata result;
        String searchString;
        Domain domain = this.session.getDomain();

        // Set up a project, and associate it with the domain for the session.
        // This means both adding the project to the domain and setting to domain
        // on the project.
        project.setId(PROJECT_ID);
        domain.getProjects().add(project);
        project.setDomain(this.session.getDomain());

        // Set up the ObjectMetadata.  This test will simply search based
        // on a static String, so other fields are not likely necessary.
        objectMetadata.setProject(project);
        objectMetadata.setPath(METADATA_PATH);

        // Set up the page for the test.
        page.setPageNumber(PAGE_NUMBER);
        page.setPageSize(PAGE_SIZE);

        // First, we should clear all previous indices from the search client.
        this.search.clear();

        // Now, attempt to index the data.  Note that this involves
        // serializing the JsonObjectMetadata.
        this.search.index(objectMetadata);

        // Now, we should flush the search client in order to ensure that
        // the indexed document can be searched.
        this.search.flush();

        searchString = METADATA_PATH;

        // First, attempt to search for the object using just to domain,
        // which got to the OMS via the project we set up.
        resultList = this.search.search(domain,
                                        searchString,
                                        page);

        // The first test is that something was, in fact, found.
        resultIterator = resultList.iterator();
        assertThat("first search has results.", resultIterator.hasNext());

        // Now we should be able to get the first result is the expected
        // value.
        result = resultIterator.next();
        assertThat("first search result contains indexed ObjectMetadata.",
                    result.getId(),
                    is(equalTo(metadataId)));

        // Finally, let's check to ensure that we only got one result.
        assertThat("first search only has one result.", (!resultIterator.hasNext()));

        // Next, we'll attempt to search with both the domain and the
        // project.
        resultList = this.search.search(domain,
                                        project,
                                        searchString,
                                        page);

        // Now, ensure that the something was found.
        resultIterator = resultList.iterator();
        assertThat("second search has results.", resultIterator.hasNext());

        // Next, we check to see if the result is the expected ObjectMetadata.
        result = resultIterator.next();
        assertThat("second search contains indexed ObjectMetadata.",
                   result.getId(),
                   is(equalTo(metadataId)));

        // Finally, ensure that there was only one result.
        assertThat("second search contains only one result.", (!resultIterator.hasNext()));

        return;
    }

}
