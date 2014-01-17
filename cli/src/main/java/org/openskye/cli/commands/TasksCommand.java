package org.openskye.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.CliExitCodeException;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.FieldBuilder;
import org.openskye.cli.commands.fields.TextField;
import org.openskye.domain.Task;
import org.openskye.domain.TaskStatus;
import org.openskye.task.step.TaskStep;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Managing the Archive Store Instances
 */
@Parameters(commandDescription = "Manage tasks")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class TasksCommand extends AbstractTaskStepCommand {

    private final String commandName = "tasks";

    @Parameter(names = "--active", description = "task id which is checked for having not yet ended")
    private String activeTaskId = null;
    @Parameter(names = "--completed", description = "task id which is checked for COMPLETED status")
    private String completedTaskId = null;
    @Parameter(names = "--failed", description = "task id which is checked for FAILED status")
    private String failedTaskId = null;

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("projectId")).build();
    }

    @Override
    public Class getClazz() {
        return Task.class;
    }

    protected void checkStatus(String taskId, Iterable<TaskStatus> expectedStatuses ) {
        Task task = getResource("tasks/" + taskId).get(Task.class);
        for ( TaskStatus status : expectedStatuses ) {
            if ( task.getStatus() == status ) {
                output.success("\nTask " + taskId + " is " + task.getStatus());
                return;
            }
        }
        throw new CliExitCodeException("Task " + taskId + " is " + task.getStatus(), 1);
    }

    @Override
    public void execute() {
        // A list of statuses which will trigger a zero exit code when using one of the options
        // that tests a Task's status.
        Set<TaskStatus> expectedStatuses = new HashSet<>();

        if ( activeTaskId != null ) {
            expectedStatuses.add(TaskStatus.CREATED);
            expectedStatuses.add(TaskStatus.QUEUED);
            expectedStatuses.add(TaskStatus.STARTED);
            checkStatus( activeTaskId, expectedStatuses );
        }  else if ( completedTaskId != null ) {
            expectedStatuses.add(TaskStatus.COMPLETED);
            checkStatus( completedTaskId, expectedStatuses );
        }  else if ( failedTaskId != null ) {
            expectedStatuses.add(TaskStatus.FAILED);
            checkStatus( failedTaskId, expectedStatuses );
        } else {
            super.execute();
        }
    }

    @Override
    public void create(TaskStep step) {
        output.message("Creating a new " + step.getLabel() + " task:\n");
        String apiDirect = "/"+step.getLabel().toLowerCase();
        Task result = (Task) getResource(getCollectionPlural()+apiDirect).post(getClazz(), step);
        saveAlias(result.getId());
        output.success(step.getLabel() + " task started at " + result.getStarted());
    }


}
