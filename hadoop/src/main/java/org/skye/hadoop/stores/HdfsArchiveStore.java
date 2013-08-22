package org.skye.hadoop.stores;

import org.omg.CORBA.portable.InputStream;
import org.skye.core.ArchiveStore;
import org.skye.core.SimpleObject;
import org.skye.domain.DomainArchiveStore;

/**
 * An implementation of an {@link ArchiveStore} that uses HDFS to store the {@link org.skye.core.ArchiveContentBlock}s
 */
public class HDFSArchiveStore implements ArchiveStore {

    @Override
    public void initialize(DomainArchiveStore das) {
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
    public void put(SimpleObject simpleObject) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InputStream getStream(SimpleObject simpleObject) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
