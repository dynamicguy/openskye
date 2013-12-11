package org.openskye.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.sun.jersey.api.client.WebResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.FieldBuilder;
import org.openskye.cli.commands.fields.TextField;
import org.openskye.cli.util.ObjectTableView;
import org.openskye.core.ObjectSet;
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
                add();
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

        PaginatedResult paginatedResult = getResource(getCollectionPlural() + objectSetID   + "/metadata/").get(PaginatedResult.class);
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
        String objectSetID = dynamicParams.get("objectSetId");
        String objectMetadataId = dynamicParams.get("objectId");

        getResource(getCollectionPlural() + "/" + objectSetID + "/" + objectMetadataId).put();
    }


}
