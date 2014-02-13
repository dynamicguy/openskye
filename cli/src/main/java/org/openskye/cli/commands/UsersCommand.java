package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.FieldBuilder;
import org.openskye.cli.commands.fields.ReferenceField;
import org.openskye.cli.commands.fields.TextField;
import org.openskye.domain.Domain;
import org.openskye.domain.User;
import org.openskye.domain.UserRole;

import java.util.List;

/**
 * A command to manage {@link User}s
 */
@Parameters(commandDescription = "Manage users")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class UsersCommand extends AbstractCrudCommand {
    @Getter
    private final String commandName = "users";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("name", false)).add(new TextField("email", false)).add(new ReferenceField(Domain.class, false)).add(new ReferenceField("userRoles",UserRole.class, true)).build();
    }

    @Override
    public Class getClazz() {
        return User.class;
    }

}
