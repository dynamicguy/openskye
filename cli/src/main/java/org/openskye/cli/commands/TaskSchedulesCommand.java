package org.openskye.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.CliException;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.FieldBuilder;
import org.openskye.domain.Identifiable;
import org.openskye.domain.TaskSchedule;
import org.openskye.task.step.TaskStep;

import java.util.List;

/**
 * Managing the TaskSchedules
 */
@Parameters(commandDescription = "Manage task schedules")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class TaskSchedulesCommand extends AbstractTaskStepCommand {
    @Parameter(names = "--delete")
    protected boolean delete;
    @Parameter
    protected String cronExpression;

    private final String commandName = "taskSchedules";

    public List<Field> getFields() {
        return FieldBuilder.start().build();
    }

    @Override
    public Class getClazz() {
        return TaskSchedule.class;
    }

    @Override
    public void execute() {
        // Ensure we are logged in
        settings.mustHaveApiKey();
        if ( delete ) {
            delete();
        } else {
            super.execute();
        }
    }

    @Override
    public void create(TaskStep step) {
        output.message("Creating a new " + step.getLabel() + " task schedule:\n");
        TaskSchedule taskSchedule = step.toTaskSchedule(cronExpression);
        Identifiable result = (Identifiable) getResource("taskSchedules").post(TaskSchedule.class, taskSchedule);
        output.success("Created task schedule with id " + result.getId());
    }

    public void delete() {
        if (id == null)
            throw new CliException("You must provide an id to delete a task schedule");
        for (String idInstance : id) {
            getResource("taskSchedules/" + idInstance).delete();
            output.success("Deleted task schedule with id " + idInstance);
        }
    }
}