package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.FieldBuilder;
import org.openskye.domain.Identifiable;
import org.openskye.domain.Task;
import org.openskye.task.step.TestTaskStep;

import java.util.List;
import java.util.UUID;

/**
 * Managing the Archive Store Instances
 */
@Parameters(commandDescription = "Manage tasks")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class TasksCommand extends AbstractCrudCommand {

    private final String commandName = "tasks";

    public List<Field> getFields() {
        return FieldBuilder.start().build();
    }

    @Override
    public Class getClazz() {
        return Task.class;
    }

    @Override
    // TODO: for now this only creates a test task.  Commands to submit other task types
    // are under construction.
    public void create() {
        output.message("Creating a new test " + getCollectionSingular() + ":\n");
        Task newObject = new TestTaskStep(UUID.randomUUID().toString(),0,0,true).toTask();
        Identifiable result = (Identifiable) getResource(getCollectionPlural()).post(getClazz(), newObject);
        output.success("Created " + getCollectionSingular() + " with id " + result.getId());
    }
}
