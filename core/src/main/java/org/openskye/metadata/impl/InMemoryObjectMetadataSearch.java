package org.openskye.metadata.impl;

import com.google.inject.Inject;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.SearchPage;
import org.openskye.core.SkyeSession;
import org.openskye.domain.Project;
import org.openskye.metadata.ObjectMetadataSearch;

import java.util.HashSet;
import java.util.Set;

/**
 * A basic, in memory implementation of the {@link ObjectMetadataSearch} with path search only
 */
public class InMemoryObjectMetadataSearch implements ObjectMetadataSearch {

    private Set<ObjectMetadata> objects = new HashSet<>();

    @Inject
    private SkyeSession session;

    @Override
    public long count(String query)
    {
        String pattern = query.replaceAll("\\*",".*");
        long count = 0;

        for ( ObjectMetadata om : objects )
        {
            if ( om.getPath().matches(pattern) )
                count++;
        }

        return count;
    }

    @Override
    public long count(Project project, String query)
    {
        String pattern = query.replaceAll("\\*",".*");
        long count = 0;

        for ( ObjectMetadata om : objects )
        {
            if ( om.getPath().matches(pattern) && om.getProject().getId().equals(project.getId()))
                count++;
        }

        return count;
    }

    @Override
    public Iterable<ObjectMetadata> search(String query)
    {
        Set<ObjectMetadata> hits = new HashSet<>();
        String pattern = query.replaceAll("\\*",".*");

        for ( ObjectMetadata om : objects )
        {
            if ( om.getPath().matches(pattern) )
                hits.add(om);
        }

        return hits;
    }

    @Override
    public Iterable<ObjectMetadata> search(Project project, String query)
    {
        Set<ObjectMetadata> hits = new HashSet<>();
        String pattern = query.replaceAll("\\*",".*");

        for ( ObjectMetadata om : objects )
        {
            if ( om.getPath().matches(pattern) && om.getProject().getId().equals(project.getId()))
                hits.add(om);

        }
        return hits;
    }

    @Override
    public Iterable<ObjectMetadata> search(String query, SearchPage searchPage)
    {
        Set<ObjectMetadata> hits = new HashSet<>();
        String pattern = query.replaceAll("\\*",".*");
        int currentResult = 0;

        for ( ObjectMetadata om : objects )
        {
            if ( om.getPath().matches(pattern) )
            {
                if(currentResult >= searchPage.getPageStart())
                {
                    hits.add(om);

                    currentResult++;

                    if(currentResult >= searchPage.getPageEnd())
                        return hits;
                }

            }
        }

        return hits;
    }

    @Override
    public Iterable<ObjectMetadata> search(Project project, String query, SearchPage searchPage)
    {
        Set<ObjectMetadata> hits = new HashSet<>();
        String pattern = query.replaceAll("\\*",".*");
        int currentResult = 0;

        for ( ObjectMetadata om : objects )
        {
            if ( om.getPath().matches(pattern) && om.getProject().getId().equals(project.getId()))
            {
                if(currentResult >= searchPage.getPageStart())
                {
                    hits.add(om);

                    currentResult++;

                    if(currentResult >= searchPage.getPageEnd())
                        return hits;
                }
            }
        }
        return hits;
    }

    @Override
    public void index(ObjectMetadata objectMetadata)
    {
        delete(objectMetadata);
        objects.add(objectMetadata);
    }

    @Override
    public void index(Iterable<ObjectMetadata> objectMetadataList)
    {
        // Generally, we'd do some bulk operation here.
        // In this case, it's not really advantageous.
        for(ObjectMetadata objectMetadata : objectMetadataList)
            index(objectMetadata);
    }

    /**
     * Clears the indexed items for the OMS.
     * <p/>
     * This is intended for testing and demo purposes.
     */
    @Override
    public void clear() {
        objects = new HashSet<>();
    }

    @Override
    public void delete()
    {
        String domainId = session.getDomain().getId();

        // With a real search implementation, if you need to loop through like this,
        // you are doing it wrong...
        for(ObjectMetadata objectMetadata : objects)
        {
            if(objectMetadata.getProject().getDomain().getId().equals(domainId))
                delete(objectMetadata);
        }
    }

    @Override
    public void delete(ObjectMetadata objectMetadata)
    {
        objects.remove(objectMetadata);
    }

    @Override
    public void delete(Iterable<ObjectMetadata> objectMetadataList)
    {
        for(ObjectMetadata objectMetadata : objectMetadataList)
            delete(objectMetadata);
    }

    @Override
    public void delete(Project project)
    {
        for(ObjectMetadata objectMetadata : objects)
        {
            if(objectMetadata.getProject().getId().equals(project.getId()))
                delete(objectMetadata);
        }
    }

    /**
     * Ensures that all indexed entries are added to internal storage.
     * <p/>
     * This is intended for testing and demo purposes.
     */
    @Override
    public void flush() {
        // No effect because this implementation is in memory only
    }
}
