package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.FieldBuilder;
import org.openskye.cli.commands.fields.ReferenceField;
import org.openskye.cli.commands.fields.TextField;
import org.openskye.domain.ArchiveStoreInstance;
import org.openskye.domain.Domain;
import org.openskye.domain.Project;

import java.util.List;

/**
 * Managing the Archive Store Instances
 */
@Parameters(commandDescription = "Manage archive store instances")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class ArchiveStoreInstancesCommand extends AbstractCrudCommand {

    private final String commandName = "archiveStores";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("name")).add(new ReferenceField("project", "projects", Project.class)).add(new TextField("implementation")).build();
    }

    @Override
    public Class getClazz() {
        return ArchiveStoreInstance.class;
    }

}
