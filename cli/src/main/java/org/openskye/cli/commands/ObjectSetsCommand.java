package org.openskye.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.sun.jersey.api.client.WebResource;
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
    @Parameter(names = "--add")
    private boolean add;

    @Override
    public void execute(){

        if(add){
            addFromSearch();
        }
        else if(list){
            list();
        }
        else if(create){
            create();
        }
        else if(delete){
            delete();
        }
    }

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

    public void addFromSearch(){
        String objectSetID;
        String domainID;
        String query;

        PaginatedResult objectSets = getResource("objectSets").get(PaginatedResult.class);
        PaginatedResult domains = getResource("domains").get(PaginatedResult.class);
        int i = 1;
        output.message("Please select an object set to add objects to: ");
        for (Object obj : objectSets.getResults()) {
            try {
                output.raw(" " + i + "/ " + BeanUtils.getProperty(obj, "name"));
            } catch (Exception e) {
                throw new SkyeException("Unable to get Object Sets" , e);
            }
            i++;
        }
        while (true) {
            String option = getConsole().readLine("Enter choice:");
            int position = Integer.parseInt(option);
            try {
                ObjectSet result = getResource("objectSets/" + BeanUtils.getProperty(objectSets.getResults().get(position - 1), "id")).get(ObjectSet.class);
                objectSetID = result.getId();
                break;
            } catch (Exception e) {
                throw new SkyeException("Unable to select Object Set ", e);
            }
        }

        i = 1;
        output.message("Please select a domain to search for objects: ");
        for (Object obj : domains.getResults()) {
            try {
                output.raw(" " + i + "/ " + BeanUtils.getProperty(obj, "name"));
            } catch (Exception e) {
                throw new SkyeException("Unable to get domains" , e);
            }
            i++;
        }
        while (true) {
            String option = getConsole().readLine("Enter choice:");
            int position = Integer.parseInt(option);
            try {
                Domain result = getResource("domains/" + BeanUtils.getProperty(domains.getResults().get(position - 1), "id")).get(Domain.class);
                domainID = result.getId();
                break;
            } catch (Exception e) {
                throw new SkyeException("Unable to select domain ", e);
            }
        }

        query = getConsole().readLine("Please enter a query: ");
        WebResource resource = client.resource(settings.getUrl() + getCollectionPlural()+"/"+objectSetID+"/search/"+domainID).queryParam("query",query);

        ObjectSet result = resource.put(ObjectSet.class);

        output.success("Successfully added objects to the Object Set with id: "+result.getId());
    }
}
