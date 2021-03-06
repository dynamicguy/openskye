package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.FieldBuilder;
import org.openskye.cli.commands.fields.ReferenceField;
import org.openskye.cli.commands.fields.TextField;
import org.openskye.domain.Domain;
import org.openskye.domain.Project;

import java.util.List;

/**
 * A command to manage {@link Project}s
 */
@Parameters(commandDescription = "Manage projects")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class ProjectsCommand extends AbstractCrudCommand {

    private final String commandName = "projects";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("name", false)).add(new TextField("description", false)).add(new ReferenceField(Domain.class, false)).build();
    }

    @Override
    public Class getClazz() {
        return Project.class;
    }

}
