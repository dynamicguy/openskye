package org.skye.hadoop.stores;

import com.google.common.base.Optional;
import org.skye.core.*;
import org.skye.domain.ArchiveStoreDefinition;
import org.skye.domain.Task;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * An implementation of an {@link ArchiveStore} that uses HDFS to store the {@link org.skye.core.ArchiveContentBlock}s
 */
public class HdfsArchiveStore implements ArchiveStore {

    @Override
    public void initialize(ArchiveStoreDefinition das) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getUrl() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSupported(SimpleObject so) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isImplementing(String implementation) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ArchiveStoreWriter getWriter(Task task) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<InputStream> getStream(ObjectMetadata metadata) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Optional<SimpleObject> getSimpleObject(ObjectMetadata metadata) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<ObjectStreamFilter> getFilters() {
        return new ArrayList<>();
    }
}
