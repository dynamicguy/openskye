package org.skye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.skye.cli.commands.fields.Field;
import org.skye.cli.commands.fields.FieldBuilder;
import org.skye.cli.commands.fields.ReferenceField;
import org.skye.cli.commands.fields.TextField;
import org.skye.domain.Domain;

import java.util.List;

/**
 * The login command
 */
@Parameters(commandDescription = "Manage metadata templates")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class MetadataTemplatesCommand extends AbstractCrudCommand {

    private final String commandName = "metadataTemplates";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("name")).add(new TextField("description")).add(new ReferenceField("domain", "domains", Domain.class)).build();
    }

    public String getCollectionName() {
        return "metadataTemplates";
    }

    public String getCollectionSingular() {
        return "metadataTemplate";
    }

    public String getCollectionPlural() {
        return "metadataTemplates";
    }

    @Override
    public Class getClazz() {
        return Domain.class;
    }

}
