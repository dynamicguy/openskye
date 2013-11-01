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
import org.openskye.filters.PathRegExFilter;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

/**
 * Managing the Archive Store Instances
 */
@Parameters(commandDescription = "Manage Channels")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class ChannelsCommand extends AbstractCrudCommand {

    private final String commandName = "channels";

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("name")).add(new ReferenceField(Project.class)).add(new ReferenceField(InformationStoreDefinition.class)).build();
    }

    @Override
    public Class getClazz() {
        return Channel.class;
    }

    @Override
    public void create() {
        output.message("Creating a new " + getCollectionSingular() + ":\n");
        Channel newObject = new Channel();

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
        ReferenceField archiveStores = new ReferenceField(ArchiveStoreDefinition.class);
        PaginatedResult paginatedResult = getResource(archiveStores.getResource()).get(PaginatedResult.class);
        if (paginatedResult.getResults().size() == 0) {
            output.message("You must have at least 1 " + archiveStores.getName() + " to create this object");
            throw new SkyeException("Objects missing that need to be created");
        } else {
            output.message("Please select archive stores to add to this channel: ");
            int i = 1;
            int j = 0;
            while (j < paginatedResult.getResults().size()) {
                for (Object obj : paginatedResult.getResults()) {
                    try {
                        output.raw(" " + i + "/ " + BeanUtils.getProperty(obj, archiveStores.getValue()));
                    } catch (Exception e) {
                        throw new SkyeException("Unable to find archive stores ", e);
                    }
                    i++;
                }
                String option = getConsole().readLine("Enter choice:");
                int position = Integer.parseInt(option);

                try {
                    ArchiveStoreDefinition chosenDef = getResource(archiveStores.getResource() + "/" + BeanUtils.getProperty(paginatedResult.getResults().get(position - 1), archiveStores.getId())).get(ArchiveStoreDefinition.class);
                    ChannelArchiveStore cas = new ChannelArchiveStore();
                    cas.setArchiveStoreDefinition(chosenDef);
                    newObject.getChannelArchiveStores().add(cas);
                    j++;
                } catch (Exception e) {
                    throw new SkyeException("Could not assign archive store", e);
                }

            }
        }
        String yesChannel = getConsole().readLine("Would you like to add filters to this definition? Y/N ");

        if(yesChannel.equalsIgnoreCase("y")){
            ChannelFilterDefinition filterDefinition = new ChannelFilterDefinition();
            filterDefinition.setImplementation(PathRegExFilter.IMPLEMENTATION);
            while(yesChannel.equalsIgnoreCase("y")){
                String regex = getConsole().readLine("Please enter a regex filter: ");
                filterDefinition.setDefinition(regex);
                newObject.getChannelFilters().add(filterDefinition);
                yesChannel = getConsole().readLine("Would you like to add more filters? Y/N: ");
            }
        }
        Channel result = (Channel) getResource(getCollectionPlural()).post(getClazz(), newObject);

        output.success("Created " + getCollectionSingular() + " with id " + result.getId());
    }


}
