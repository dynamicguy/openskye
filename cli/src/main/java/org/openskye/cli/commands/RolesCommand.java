package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.AbstractCrudCommand;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.FieldBuilder;
import org.openskye.cli.commands.fields.TextField;
import org.openskye.domain.Domain;

import java.util.List;

/**
 * Managing the Roles
 */
@Parameters(commandDescription = "Manage roles")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class RolesCommand extends AbstractCrudCommand {

    private final String commandName = "roles";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("name")).build();
    }

    public String getCollectionName() {
        return "roles";
    }

    public String getCollectionSingular() {
        return "role";
    }

    public String getCollectionPlural() {
        return "roles";
    }

    @Override
    public Class getClazz() {
        return Domain.class;
    }

}
