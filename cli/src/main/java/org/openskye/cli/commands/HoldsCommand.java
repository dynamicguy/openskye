package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.FieldBuilder;
import org.openskye.cli.commands.fields.TextField;
import org.openskye.domain.Hold;

import java.util.List;

/**
 * Manage {@link Hold}s from the command line
 */
@Parameters(commandDescription = "Manage holds")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class HoldsCommand extends AbstractCrudCommand {

    private final String commandname = "holds";

    @Override
    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("name")).add(new TextField("description")).add(new TextField("query")).build();
    }

    @Override
    public String getCommandName() {
        return commandname;
    }

    @Override
    public Class getClazz() {
        return Hold.class;
    }
}
