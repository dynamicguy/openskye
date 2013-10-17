package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.FieldBuilder;
import org.openskye.cli.commands.fields.ReferenceField;
import org.openskye.cli.commands.fields.TextField;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.Domain;

import java.util.List;

/**
 * The login command
 */
@Parameters(commandDescription = "Manage archive store definition")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class ArchiveStoreDefinitionsCommand extends AbstractCrudCommand {

    private final String commandName = "archiveStoreDefinitions";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("name")).add(new TextField("description")).add(new ReferenceField("domain", "domains", Domain.class)).build();
    }

    public String getCollectionName() {
        return "archiveStoreDefinitions";
    }

    public String getCollectionSingular() {
        return "archiveStoreDefinition";
    }

    public String getCollectionPlural() {
        return "archiveStoreDefinitions";
    }

    @Override
    public Class getClazz() {
        return ArchiveStoreDefinition.class;
    }

}
