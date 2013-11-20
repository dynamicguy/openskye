package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.openskye.cli.commands.fields.*;
import org.openskye.core.ObjectSet;
import org.openskye.core.SkyeException;
import org.openskye.domain.*;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.task.step.*;

import java.io.Console;
import java.lang.reflect.InvocationTargetException;
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
        return FieldBuilder.start().add(new TextField("workerName")).add(new TextField("projectId")).build();
    }

    @Override
    public Class getClazz() {
        return Task.class;
    }

    @Override
    public void create(TaskStep step) {
        output.message("Creating a new " + step.getLabel() + " task:\n");
        Task task = step.toTask();
        String apiDirect = "/"+step.getLabel().toLowerCase();
        Identifiable result = (Identifiable) getResource(getCollectionPlural()+apiDirect).post(getClazz(), step);
        output.success("Created task with id " + result.getId());
    }


}
