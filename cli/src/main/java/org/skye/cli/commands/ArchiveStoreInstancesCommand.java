package org.skye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.skye.cli.commands.fields.Field;
import org.skye.cli.commands.fields.FieldBuilder;
import org.skye.cli.commands.fields.TextField;
import org.skye.domain.Domain;

import java.util.List;

/**
 * The login command
 */
@Parameters(commandDescription = "Manage archive store instances")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class ArchiveStoreInstancesCommand extends AbstractCrudCommand {

    private final String commandName = "archiveStoreInstances";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("name")).add(new TextField("implementation")).build();
    }

    public String getCollectionName() {
        return "archiveStoreInstances";
    }

    public String getCollectionSingular() {
        return "archiveStoreInstances";
    }

    public String getCollectionPlural() {
        return "archiveStoreInstances";
    }

    @Override
    public Class getClazz() {
        return Domain.class;
    }

}
