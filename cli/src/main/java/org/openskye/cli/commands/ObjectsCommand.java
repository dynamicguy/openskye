package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.FieldBuilder;
import org.openskye.cli.commands.fields.PropertiesField;
import org.openskye.cli.commands.fields.TextField;

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
    @Override
    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("path")).add(new TextField("implementation")).add(new PropertiesField("metadata")).build();
    }

    @Override
    public Class getClazz() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getCommandName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
