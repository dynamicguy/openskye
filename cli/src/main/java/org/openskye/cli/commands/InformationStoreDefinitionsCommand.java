package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.*;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.InformationStoreDefinition;
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

    private final String commandName = "informationStores";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("name")).add(new ReferenceField(Project.class)).add(new TextField("implementation")).add(new PropertiesField("properties")).build();
    }

    @Override
    public Class getClazz() {
        return InformationStoreDefinition.class;
    }

}
