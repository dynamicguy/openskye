package org.skye.job;

import org.skye.domain.Channel;

/**
 * Representation of a job that destroys a set of {@link org.skye.core.SimpleObject}
 */
public interface ArchiveJob extends Job {

    /**
     * Request that we archive the given {@link Channel}
     *
     * @param channel The channel to archive
     */
    void archive(Channel channel);

}
