package org.openskye.task.step;

import lombok.extern.slf4j.Slf4j;
import org.openskye.core.ArchiveStore;
import org.openskye.core.InformationStore;
import org.openskye.core.SkyeException;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.domain.Task;

/**
 * A {@link TaskStep} that handles a task type of TEST, which is mainly used to test the
 * task manager.  TEST tasks just sleep and generate simulated log output.
 */
@Slf4j
public class TestTaskStep extends AbstractTaskStep  {

    Task task;

    public TestTaskStep(Task task) {
        this.task = task;
    }

    @Override
    public void validate() {
        // TEST tasks require no validation
    }

    @Override
    public void start() {
        // The task parameter string has the form (sleep_seconds):(iterations):(PASS|FAIL), for example
        // "10:3:PASS" means to sleep for 10 seconds x 3 iterations and pass at the end
        try {
            String[] param = task.getTaskParameters().split(":",3);
            int sleepSeconds = Integer.parseInt(param[0]);
            int iterations = Integer.parseInt(param[1]);
            boolean pass = (param[2].toUpperCase().equals("PASS"));
            log.info("Test Task: sleepSeconds="+sleepSeconds+" iterations="+iterations+" pass="+pass);
            for ( int i=0; i<iterations; i++ ) {
                Thread.sleep(sleepSeconds*1000L);
                log.info("Test Task Sleeping ... i="+i);
            }
            if ( !pass ) {
                throw new SkyeException("Test Task Final Status = "+param[2]);
            }
        } catch (InterruptedException ie) {
            log.info("Test Task Interrupted");
        }
    }

    @Override
    protected InformationStore buildInformationStore(InformationStoreDefinition dis) {
        return null;
    }

    @Override
    protected ArchiveStore buildArchiveStore(ArchiveStoreDefinition archiveStoreDefinition) {
        return null;
    }


}
