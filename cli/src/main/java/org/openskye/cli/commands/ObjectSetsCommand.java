package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.openskye.cli.commands.fields.*;
import org.openskye.core.ObjectSet;
import org.openskye.core.SkyeException;
import org.openskye.domain.*;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.filters.PathRegExFilter;

import java.io.Console;
import java.util.List;

/**
 * Managing the Archive Store Instances
 */
@Parameters(commandDescription = "Manage Object Sets")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class ObjectSetsCommand extends AbstractCrudCommand {

    private final String commandName = "objectSets";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("name")).build();
    }

    @Override
    public Class getClazz() {
        return ObjectSet.class;
    }

    @Override
    public String getCommandName() {
        return commandName;
    }

    @Override
    public void create(){
        output.message("Creating a new " + getCollectionSingular() + ":\n");
        ObjectSet newObject = new ObjectSet();

        Console console = getConsole();
        for (Field field : getFields()) {
            String attributeName = field.getName();
            if (field instanceof TextField) {
                String newValue = console.readLine(StringUtils.capitalize(attributeName) + ": ");
                try {
                    BeanUtils.setProperty(newObject, attributeName, newValue);
                } catch (Exception e) {
                    throw new SkyeException("Unable to set property " + attributeName + " on " + newObject + " to " + newValue);
                }
            } else if (field instanceof ReferenceField) {
                selectReferenceField((ReferenceField) field, newObject);
            } else if (field instanceof PropertiesField) {
                setPropertiesField((PropertiesField) field, newObject);
            } else if (field instanceof EnumerationField) {
                selectEnum((EnumerationField) field, newObject);
            }
        }
        ObjectSet result = (ObjectSet)getResource(getCollectionPlural()).post(getClazz(), newObject);

        output.success("Created " + getCollectionSingular() + " with id " + result.getId());
    }
}
