package org.skye.job.impl;

import org.skye.core.ContainerObject;
import org.skye.core.InformationStore;
import org.skye.core.SimpleObject;
import org.skye.domain.Channel;
import org.skye.job.DiscoverJob;

/**
 * The DiscoveryJob implementation that is used by default
 */
public class DiscoverJobImpl extends AbstractJob implements DiscoverJob {

    @Override
    public void discover(Channel channel) {
        InformationStore is = buildInformationStore(channel.getDomainInformationStore());
        discover(is, is.getRoot());
    }

    private void discover(InformationStore is, Iterable<SimpleObject> simpleObjects) {
        for (SimpleObject simpleObject : simpleObjects) {
            if (simpleObject instanceof ContainerObject)
                discover(is, is.getChildren(simpleObject));
            else
                omr.put(simpleObject);
        }
    }


}
