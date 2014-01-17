package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.FieldBuilder;
import org.openskye.cli.commands.fields.TextField;
import org.openskye.domain.Node;

import java.util.List;

/**
 * A command to manage the {@link org.openskye.domain.Node}s.
 */
@Parameters(commandDescription = "Manage nodes")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class NodesCommand extends AbstractCrudCommand {

    private final String commandName = "nodes";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("hostname")).build();
    }

    @Override
    public Class getClazz() {
        return Node.class;
    }

}
