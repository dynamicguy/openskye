package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.openskye.cli.commands.fields.*;
import org.openskye.core.SkyeException;
import org.openskye.domain.*;
import org.openskye.domain.dao.PaginatedResult;

import java.io.Console;
import java.util.List;

/**
 * Managing the Archive Store Instances
 */
@Parameters(commandDescription = "Manage tasks")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class TasksCommand extends AbstractCrudCommand {

    private final String commandName = "tasks";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new EnumerationField("taskType", TaskType.class)).add(new ReferenceField(Channel.class)).build();
    }

    @Override
    public Class getClazz() {
        return Task.class;
    }

    @Override
    public void create() {
        output.message("Creating a new " + getCollectionSingular() + ":\n");
        Task newObject = new Task();

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
        if (newObject.getTaskType() == TaskType.EXTRACT|| newObject.getTaskType()==TaskType.DESTROY) {
            if (newObject.getTargetInformationStoreDefinition() == null) {
                output.message("You have chosen a task that requires a target information store. Please select an information store to extract your data to: ");

                ReferenceField informationStores = new ReferenceField(InformationStoreDefinition.class);
                PaginatedResult paginatedResult = getResource(informationStores.getResource()).get(PaginatedResult.class);
                int i = 1;


                for (Object obj : paginatedResult.getResults()) {
                    try {
                        output.raw(" " + i + "/ " + BeanUtils.getProperty(obj, informationStores.getValue()));
                    } catch (Exception e) {
                        throw new SkyeException("Unable to find information stores ", e);
                    }
                    i++;
                }

                String option = getConsole().readLine("Enter choice:");
                int position = Integer.parseInt(option);

                try {
                    InformationStoreDefinition chosenDef = getResource(informationStores.getResource() + "/" + BeanUtils.getProperty(paginatedResult.getResults().get(position - 1), informationStores.getId())).get(InformationStoreDefinition.class);
                    newObject.setTargetInformationStoreDefinition(chosenDef);

                } catch (Exception e) {
                    throw new SkyeException("Could not assign information store", e);
                }

            }
        }
        Identifiable result = (Identifiable) getResource(getCollectionPlural()).post(getClazz(), newObject);

        output.success("Created " + getCollectionSingular() + " with id " + result.getId());
    }

}
