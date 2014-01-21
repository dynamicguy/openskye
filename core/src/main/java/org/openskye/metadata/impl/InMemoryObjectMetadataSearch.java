package org.openskye.metadata.impl;

import org.openskye.core.ObjectMetadata;
import org.openskye.core.SearchPage;
import org.openskye.domain.Project;
import org.openskye.metadata.ObjectMetadataSearch;

import java.util.HashSet;
import java.util.Set;

/**
 * A basic, in memory implementation of the {@link ObjectMetadataSearch} with path search only
 */
public class InMemoryObjectMetadataSearch implements ObjectMetadataSearch {

    private Set<ObjectMetadata> objects = new HashSet<>();

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
    public void index(ObjectMetadata objectMetadata) {
        objects.add(objectMetadata);
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
