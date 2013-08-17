package org.skye.job;

import org.skye.domain.Channel;

/**
 * Representation of a job that performs discovery on a {@link org.skye.core.InformationStore}
 */
public interface DiscoverJob extends Job {

    /**
     * Will update the {@link org.skye.metadata.ObjectMetadataRepository} with
     * information from this {@link Channel}
     *
     * @param channel The Channel to run discovery against
     */
    void discover(Channel channel);

}
