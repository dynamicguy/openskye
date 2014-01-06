package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.FieldBuilder;
import org.openskye.cli.commands.fields.ReferenceField;
import org.openskye.domain.Project;
import org.openskye.domain.ProjectUser;
import org.openskye.domain.User;

import java.util.List;

/**
 * A command to manage {@link ProjectUser}s
 */
@Parameters(commandDescription = "Manage users on a project")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class ProjectUsersCommand extends AbstractCrudCommand {

    private final String commandName = "projectUsers";

    @Override
    public List<Field> getFields() {
        return FieldBuilder.start().add(new ReferenceField(User.class)).add(new ReferenceField(Project.class)).build();
    }

    @Override
    public String getCommandName() {
        return commandName;
    }

    @Override
    public Class getClazz() {
        return ProjectUser.class;
    }
}
