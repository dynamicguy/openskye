package org.skye.hadoop;

import com.google.inject.persist.PersistService;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: joshua
 * Date: 9/26/13
 * Time: 10:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class HBaseJPAInitializer {
    @Inject
    public HBaseJPAInitializer(final PersistService service) {
        service.start();
    }
}
