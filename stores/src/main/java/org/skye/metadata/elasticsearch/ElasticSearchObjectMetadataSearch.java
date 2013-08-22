package org.skye.metadata.elasticsearch;

import org.skye.core.SimpleObject;
import org.skye.domain.Domain;
import org.skye.domain.Project;
import org.skye.metadata.ObjectMetadataSearch;
import org.skye.util.Page;

/**
 * An implementation of
 */
public class ElasticSearchObjectMetadataSearch implements ObjectMetadataSearch {
    @Override
    public Iterable<SimpleObject> search(Domain domain, String query, Page page) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<SimpleObject> search(Domain domain, Project project, String query, Page page) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void index(SimpleObject simpleObject) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
