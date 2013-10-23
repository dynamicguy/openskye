package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.*;
import org.openskye.domain.*;

import java.util.List;

/**
 * Managing the Archive Store Instances
 */
@Parameters(commandDescription = "Manage Channels")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class ChannelsCommand extends AbstractCrudCommand {

    private final String commandName = "channels";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("name")).add(new ReferenceField(Project.class)).add(new ReferenceField(InformationStoreDefinition.class)).build();
    }

    @Override
    public Class getClazz() {
        return Channel.class;
    }

}
