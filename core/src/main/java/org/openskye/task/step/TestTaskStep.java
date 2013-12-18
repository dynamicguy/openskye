package org.openskye.task.step;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.openskye.core.SkyeException;
import org.openskye.domain.Project;
import org.openskye.domain.TaskStatus;

/**
 * A {@link TaskStep} that handles a task type of TEST, which is mainly used to test the
 * task manager.  TEST tasks just sleep and generate simulated log output.
 */
@Slf4j
@NoArgsConstructor
public class TestTaskStep extends TaskStep {
    @JsonProperty
    @Getter
    @Setter
    private Project project;
    @Getter
    @Setter
    private Integer sleepSeconds;
    @Getter
    @Setter
    private Integer iterations;
    @Getter
    @Setter
    private Boolean pass;

    public TestTaskStep(Project project, Integer sleepSeconds, Integer iterations, Boolean pass) {
        this.sleepSeconds = sleepSeconds;
        this.iterations = iterations;
        this.pass = pass;
        this.project = project;
    }

    @Override
    public Project getStepProject() {
        return this.project;
    }

    @Override
    public String getLabel() {
        return "TEST";
    }

    @Override
    public void rehydrate() {
        // Nothing to do
    }

    @Override
    public void validate() {
        // TEST tasks require no validation
    }

    @Override
    public TaskStatus call() throws Exception {
        try {
            log.info("Test Task: sleepSeconds=" + sleepSeconds + " iterations=" + iterations + " pass=" + pass);
            for (int i = 0; i < iterations; i++) {
                Thread.sleep(sleepSeconds * 1000L);
                log.info("Test Task Sleeping ... i=" + i);
            }
            if (!pass) {
                throw new SkyeException("Test Task Set to Fail");
            }
        } catch (InterruptedException ie) {
            log.info("Test Task Interrupted");
            return TaskStatus.ABORTED;
        }

        return TaskStatus.COMPLETED;
    }

}
