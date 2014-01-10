package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.FieldBuilder;
import org.openskye.cli.commands.fields.TextField;
import org.openskye.domain.Task;
import org.openskye.task.step.TaskStep;

import java.util.List;

/**
 * Managing the Archive Store Instances
 */
@Parameters(commandDescription = "Manage tasks")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class TasksCommand extends AbstractTaskStepCommand {

    private final String commandName = "tasks";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("projectId")).build();
    }

    @Override
    public Class getClazz() {
        return Task.class;
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
