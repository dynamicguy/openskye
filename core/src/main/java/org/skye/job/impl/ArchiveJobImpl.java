package org.skye.job.impl;

import org.skye.core.ArchiveStore;
import org.skye.core.InformationStore;
import org.skye.core.SimpleObject;
import org.skye.domain.Channel;
import org.skye.domain.ChannelArchiveStore;
import org.skye.job.ArchiveJob;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the {@link ArchiveJob}
 */
public class ArchiveJobImpl extends AbstractJob implements ArchiveJob {

    private Map<ChannelArchiveStore, ArchiveStore> channelStoreMap = new HashMap<>();

    @Override
    public void archive(Channel channel) {

        // Build up the information and archive stores

        InformationStore is = buildInformationStore(channel.getDomainInformationStore());
        for (ChannelArchiveStore cas : channel.getChannelArchiveStores()) {
            channelStoreMap.put(cas, buildArchiveStore(cas.getDomainArchiveStore()));
        }

        // Based on the fact that we have done discovery then we will
        // look for all SimpleObject's that from the OMR

        for (SimpleObject simpleObject : omr.getSimpleObjects(channel.getDomainInformationStore())) {
            for (ChannelArchiveStore cas : channel.getChannelArchiveStores()) {
                channelStoreMap.get(cas).put(simpleObject);
            }
        }
    }


}
