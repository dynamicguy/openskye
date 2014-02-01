package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.*;
import org.openskye.cli.util.ArchiveStoreImplementation;
import org.openskye.domain.ArchiveStoreInstance;

import java.util.List;

/**
 * A command to manage the {@link ArchiveStoreInstance}s.
 */
@Parameters(commandDescription = "Manage archive store instances")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class ArchiveStoreInstancesCommand extends AbstractCrudCommand {

    private final String commandName = "archiveStoreInstances";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("name")).add(new EnumerationField("implementation", ArchiveStoreImplementation.class))
                .add(new NodeRolesField("nodes")).add(new PropertiesField("properties")).build();
    }

    @Override
    public Class getClazz() {
        return ArchiveStoreInstance.class;
    }

}
