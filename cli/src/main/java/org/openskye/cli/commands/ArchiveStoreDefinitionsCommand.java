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
 * A command to manage the {@link ArchiveStoreDefinition}s.
 */
@Parameters(commandDescription = "Manage archive store definitions")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class ArchiveStoreDefinitionsCommand extends AbstractCrudCommand {

    private final String commandName = "archiveStoreDefinitions";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("name", false)).add(new ReferenceField(Project.class, false)).add(new ReferenceField(ArchiveStoreInstance.class, false)).add(new PropertiesField("properties", false)).build();
    }

    @Override
    public Class getClazz() {
        return ArchiveStoreDefinition.class;
    }

}
