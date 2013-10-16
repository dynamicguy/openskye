package org.skye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.skye.cli.commands.fields.Field;
import org.skye.cli.commands.fields.FieldBuilder;
import org.skye.cli.commands.fields.ReferenceField;
import org.skye.cli.commands.fields.TextField;
import org.skye.domain.ArchiveStoreDefinition;
import org.skye.domain.Domain;

import java.util.List;

/**
 * The login command
 */
@Parameters(commandDescription = "Manage information store definitions")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class InformationStoreDefinitionsCommand extends AbstractCrudCommand {

    private final String commandName = "informationStoreDefinitions";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("name")).add(new ReferenceField("domain", "domains", Domain.class)).add(new TextField("implementation")).build();
    }

    public String getCollectionName() {
        return "informationStoreDefinitions";
    }

    public String getCollectionSingular() {
        return "informationStoreDefinition";
    }

    public String getCollectionPlural() {
        return "informationStoreDefinitions";
    }

    @Override
    public Class getClazz() {
        return ArchiveStoreDefinition.class;
    }

}