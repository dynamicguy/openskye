package org.openskye.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.sun.jersey.api.client.WebResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.FieldBuilder;
import org.openskye.cli.commands.fields.TextField;
import org.openskye.cli.util.ObjectTableView;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.ObjectSet;
import org.openskye.core.SkyeException;
import org.openskye.domain.dao.PaginatedResult;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
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
    @Parameter(names = "--objects")
    private boolean objects;

    @Override
    public void execute() {

        if (add) {
            if (dynamicParams.get("query") != null) { //did the user add -P query=...
                addFromSearch();
            } else {
                addFromSearch();
            }
        } else if (list) {
            list();
        } else if (objects) {
            listObjects();
        } else if (create) {
            create();
        } else if (delete) {
            delete();
        }
    }

    private void listObjects() {
        String objectSetID = dynamicParams.get("objectSetId");

        PaginatedResult paginatedResult = getResource(getCollectionPlural() + "/metadata/" + objectSetID).get(PaginatedResult.class);
        List<String> fieldsWithId = new ArrayList<>();
        fieldsWithId.add("id");
        fieldsWithId.addAll(new ObjectsCommand().getFieldNames());

        if (paginatedResult.getResults().size() > 0) {
            output.message("Listing objects");

            ObjectTableView tableView = new ObjectTableView(paginatedResult, fieldsWithId);
            output.insertLines(1);
            tableView.draw(output);
            output.success("\nFound " + paginatedResult.getResults().size() + " objects");

        } else {
            output.success("\nNo " + getCollectionPlural() + " found");

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

    public void addFromSearch() {
        String objectSetID = dynamicParams.get("objectSetId");
        String query = dynamicParams.get("query");


        WebResource resource = client.resource(settings.getUrl() + getCollectionPlural() + "/" + objectSetID + "/search").queryParam("query", query);
        WebResource.Builder builder = resource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE);
        if (settings.getApiKey() != null) {
            builder.header("X-Api-Key", settings.getApiKey());
        }
        ObjectSet result = builder.put(ObjectSet.class);

        output.success("Successfully added objects to the Object Set with id: " + result.getId());
    }

    public void add() {
        String objectSetID;
        String objectMetadataId;

        PaginatedResult objectSets = getResource("objectSets").get(PaginatedResult.class);
        PaginatedResult objectMetadata = getResource("objects").get(PaginatedResult.class);

        int i = 1;
        output.message("Please select an object set to add objects to: ");
        for (Object obj : objectSets.getResults()) {
            try {
                output.raw(" " + i + "/ " + BeanUtils.getProperty(obj, "name"));
            } catch (Exception e) {
                throw new SkyeException("Unable to get Object Sets", e);
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
        output.message("Please select an object to add to this set: ");
        for (Object obj : objectMetadata.getResults()) {
            try {
                output.raw(" " + i + "/ " + BeanUtils.getProperty(obj, "path"));
            } catch (Exception e) {
                throw new SkyeException("Unable to get Object Sets", e);
            }
            i++;
        }
        while (true) {
            String option = getConsole().readLine("Enter choice:");
            int position = Integer.parseInt(option);
            try {
                ObjectMetadata result = getResource("objects/" + BeanUtils.getProperty(objectMetadata.getResults().get(position - 1), "id")).get(ObjectMetadata.class);
                objectMetadataId = result.getId();
                break;
            } catch (Exception e) {
                throw new SkyeException("Unable to select Object ", e);
            }
        }
        getResource(getCollectionPlural() + "/" + objectSetID + "/" + objectMetadataId).put();
    }


}
