package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import com.google.inject.Inject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.*;
import org.openskye.core.ObjectMetadata;
import org.openskye.domain.Project;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: atcmostafavi
 * Date: 11/11/13
 * Time: 1:16 PM
 * To change this template use File | Settings | File Templates.
 */
@Parameters(commandDescription = "Manage Object Metadata")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class ObjectsCommand extends AbstractCrudCommand {

    @Inject
    private final String commandName = "objects";

    @Override
    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("path")).add(new TextField("implementation")).add(new ReferenceField(Project.class)).add(new PropertiesField("metadata")).build();
    }

    @Override
    public Class getClazz() {
        return ObjectMetadata.class;
    }

    @Override
    public String getCollectionPlural() {
        return "objects";
    }

}
