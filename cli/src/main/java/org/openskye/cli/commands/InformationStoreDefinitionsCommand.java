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
import org.openskye.domain.Project;

import java.util.List;

/**
 * Managing the Information Store Definitions
 */
@Parameters(commandDescription = "Manage information store definitions")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class InformationStoreDefinitionsCommand extends AbstractCrudCommand {

    private final String commandName = "informationStoreDefinitions";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("name")).add(new ReferenceField("project", "projects", Project.class)).add(new TextField("implementation")).build();
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
