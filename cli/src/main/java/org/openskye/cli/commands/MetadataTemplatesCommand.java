package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.FieldBuilder;
import org.openskye.cli.commands.fields.ReferenceField;
import org.openskye.cli.commands.fields.TextField;
import org.openskye.domain.Domain;

import java.util.List;

/**
 * Managing the Metadata Templates
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
