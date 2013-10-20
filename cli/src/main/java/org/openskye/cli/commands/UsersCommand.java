package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.FieldBuilder;
import org.openskye.cli.commands.fields.TextField;
import org.openskye.domain.Domain;

import java.util.List;

/**
 * Managing the Users
 */
@Parameters(commandDescription = "Manage users")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class UsersCommand extends AbstractCrudCommand {

    private final String commandName = "users";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("name")).add(new TextField("email")).add(new TextField("password")).build();
    }

    public String getCollectionName() {
        return "users";
    }

    public String getCollectionSingular() {
        return "user";
    }

    public String getCollectionPlural() {
        return "users";
    }

    @Override
    public Class getClazz() {
        return Domain.class;
    }

}
