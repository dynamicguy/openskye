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
import org.openskye.domain.Project;

import java.util.List;

/**
 * The login command
 */
@Parameters(commandDescription = "Manage projects")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class ProjectsCommand extends AbstractCrudCommand {

    private final String commandName = "projects";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new ReferenceField("domain", "domains", Domain.class)).add(new TextField("name")).build();
    }

    public String getCollectionName() {
        return "projects";
    }

    public String getCollectionSingular() {
        return "project";
    }

    public String getCollectionPlural() {
        return "projects";
    }

    @Override
    public Class getClazz() {
        return Project.class;
    }

}
