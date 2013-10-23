package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.*;
import org.openskye.domain.ArchiveStoreDefinition;
import org.openskye.domain.ArchiveStoreInstance;
import org.openskye.domain.Project;

import java.util.List;

/**
 * Managing the Archive Store Definitions
 */
@Parameters(commandDescription = "Manage archive store definition")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class ArchiveStoreDefinitionsCommand extends AbstractCrudCommand {

    private final String commandName = "archiveStoreDefinitions";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("name")).add(new ReferenceField(Project.class)).add(new ReferenceField("archiveStoreInstance", "archiveStoreInstances", ArchiveStoreInstance.class)).add(new TextField("implementation")).add(new PropertiesField("properties")).build();
    }

    @Override
    public Class getClazz() {
        return ArchiveStoreDefinition.class;
    }

}
