package org.skye.stores.archive;

import org.skye.core.ArchiveStore;
import org.skye.core.SimpleObject;

import java.util.Properties;

/**
 * An implementation of an {@link ArchiveStore} that simply uses the local
 * filesystem to store archives
 */
public class LocalFilesystemArchiveStore implements ArchiveStore {

    public final static String IMPLEMENTATION = "localFS";

    @Override
    public void initialize(Properties properties) {
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
        return implementation.equals(IMPLEMENTATION);
    }

    @Override
    public void put(SimpleObject simpleObject) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
