package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.FieldBuilder;
import org.openskye.cli.commands.fields.ReferenceField;
import org.openskye.domain.Role;
import org.openskye.domain.User;
import org.openskye.domain.UserRole;

import java.util.List;

/**
 * A command to manage {@link UserRole}s
 */
@Parameters(commandDescription = "Manage user/roles")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class UserRolesCommand extends AbstractCrudCommand {

    private final String commandName = "userRoles";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new ReferenceField(User.class, false)).add(new ReferenceField(Role.class, false)).build();
    }

    @Override
    public Class getClazz() {
        return UserRole.class;
    }

}
