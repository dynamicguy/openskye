package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.FieldBuilder;
import org.openskye.cli.commands.fields.TextField;
import org.openskye.domain.ArchiveStoreDefinition;

import java.util.List;

/**
 * Managing the Archive Store Definitions
 */
@Parameters(commandDescription = "Manage archive store definition")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class ArchiveStoreDefinitionsCommand extends AbstractCrudCommand {

    private final String commandName = "archiveStores";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("name")).add(new TextField("description")).build();
    }

    @Override
    public Class getClazz() {
        return ArchiveStoreDefinition.class;
    }

}
