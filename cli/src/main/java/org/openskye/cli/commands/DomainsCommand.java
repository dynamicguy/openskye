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
 * The login command
 */
@Parameters(commandDescription = "Manage domains")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class DomainsCommand extends AbstractCrudCommand {

    private final String commandName = "domains";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("name")).build();
    }

    public String getCollectionName() {
        return "domains";
    }

    public String getCollectionSingular() {
        return "domain";
    }

    public String getCollectionPlural() {
        return "domains";
    }

    @Override
    public Class getClazz() {
        return Domain.class;
    }

}