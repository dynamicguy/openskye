package org.openskye.cli.commands;

import com.beust.jcommander.Parameters;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.openskye.cli.commands.fields.*;
import org.openskye.core.SkyeException;
import org.openskye.domain.*;
import org.openskye.filters.PathRegExFilter;

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

        for (Field field : getFields()) {
            String attributeName = field.getName();
            String attributeVal = dynamicParams.get(attributeName);
            if (field instanceof TextField) {
                String newValue = attributeVal;
                try {
                    BeanUtils.setProperty(newObject, attributeName, newValue);
                    output.raw(newObject.toString());
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

        if(dynamicParams.get("archiveStores")!=null){
            String archiveStores = dynamicParams.get("archiveStores");
            String[] storeIds = archiveStores.split(",");
            for(String id : storeIds) {

            try {
                    ArchiveStoreDefinition chosenDef = getResource("archiveStoreDefinitions/" + resolveAlias(id)).get(ArchiveStoreDefinition.class);
                    ChannelArchiveStore cas = new ChannelArchiveStore();
                    cas.setArchiveStoreDefinition(chosenDef);
                    newObject.getChannelArchiveStores().add(cas);

                } catch (Exception e) {
                    throw new SkyeException("Could not assign archive store", e);
                }


            }
        }
        if(dynamicParams.get("filters")!=null){
            ChannelFilterDefinition filterDefinition = new ChannelFilterDefinition();
            filterDefinition.setImplementation(PathRegExFilter.IMPLEMENTATION);
            String[] filters = dynamicParams.get("filters").split(",");
            for(String filter : filters){
                String regex = filter;
                filterDefinition.setDefinition(regex);
                newObject.getChannelFilters().add(filterDefinition);
            }
        }
        Channel result = (Channel) getResource(getCollectionPlural()).post(getClazz(), newObject);
        saveAlias(result.getId());
        output.success("Created " + getCollectionSingular() + " with id " + result.getId());
    }


}
