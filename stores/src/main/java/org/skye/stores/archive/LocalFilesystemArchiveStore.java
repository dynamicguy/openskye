package org.skye.stores.archive;

import org.skye.core.ArchiveStore;
import org.skye.core.SimpleObject;
import org.skye.domain.DomainArchiveStore;

import javax.tools.FileObject;

/**
 * An implementation of an {@link ArchiveStore} that simply uses the local
 * filesystem to store archives
 */
public class LocalFilesystemArchiveStore implements ArchiveStore {

    public final static String IMPLEMENTATION = "localFS";
    private DomainArchiveStore domainArchiveStore;

    @Override
    public void initialize(DomainArchiveStore das) {
        this.domainArchiveStore = das;
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
        return true;
    }

    @Override
    public boolean isImplementing(String implementation) {
        return implementation.equals(IMPLEMENTATION);
    }

    @Override
    public void put(SimpleObject simpleObject) {
        if (simpleObject instanceof FileObject) {

        }
    }
}
