package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.FieldBuilder;
import org.openskye.cli.commands.fields.ReferenceField;
import org.openskye.domain.Domain;
import org.openskye.domain.Role;
import org.openskye.domain.User;
import org.openskye.domain.UserRole;

import java.util.List;

/**
 * Managing the User Roles
 */
@Parameters(commandDescription = "Manage user/roles")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class UserRolesCommand extends AbstractCrudCommand {

    private final String commandName = "userRoles";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new ReferenceField("user", "users", User.class)).add(new ReferenceField("role", "roles", Role.class)).build();
    }

    public String getCollectionName() {
        return "userRoles";
    }

    public String getCollectionSingular() {
        return "userRole";
    }

    public String getCollectionPlural() {
        return "userRoles";
    }

    @Override
    public Class getClazz() {
        return UserRole.class;
    }

}
