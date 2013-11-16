package org.openskye.task.step;

import org.openskye.core.SkyeException;
import org.openskye.domain.TaskStatus;

/**
 * Implementation of a verification of a set of {@link org.openskye.core.SimpleObject}
 */
public class VerifyTaskStep extends TaskStep {

    @Override
    public String getLabel() {
        return "VERIFY";
    }

    @Override
    public void validate() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TaskStatus call() throws Exception {
        throw new SkyeException("Verify tasks are not yet implemented");
    }
}
