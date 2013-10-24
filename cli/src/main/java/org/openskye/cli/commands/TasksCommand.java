package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.*;
import org.openskye.domain.*;

import java.util.List;

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
        return FieldBuilder.start().add(new EnumerationField("task type", TaskType.class)).add(new ReferenceField("parent task", Task.class)).add(new ReferenceField(Project.class)).add(new ReferenceField(TaskStatistics.class)).build();
    }

    @Override
    public Class getClazz() {
        return Task.class;
    }

}
