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
 * A command to manage {@link ObjectSet}s.
 */
@Parameters(commandDescription = "Manage Object Sets")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class ObjectSetsCommand extends AbstractCrudCommand {

    private final String commandName = "objectSets";
    /**
     * A JCommand parameter which represents a request to add to an {@link ObjectSet}. {@link #add()} or {@link
     * #addFromSearch()} is called if this parameter is set to true (set by the user by adding --add to the end of their
     * command).
     */
    @Parameter(names = "--add")
    private boolean add;
    /**
     * A JCommand parameter which represents a request to list the objects in an {@link ObjectSet}. {@link
     * #listObjects()} is called if this parameter is set to true (set by the user by adding --objects to the end of
     * their command).
     */
    @Parameter(names = "--objects")
    private boolean objects;

    @Override
    public void execute() {

        if (add) {
            if (dynamicParams.get("query") != null) { //did the user add -P query=
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

    /**
     * Lists all the objects in an object set. The object set is specified by the objectSetId (provided by the user
     * through the dynamic parameters). This id is sent to {@link org.openskye.resource.ObjectSetResource#getObjects(String)},
     * and the result is printed in tabular form in the console.
     */
    private void listObjects() {
        String objectSetID = dynamicParams.get("objectSetId");

        PaginatedResult paginatedResult = getResource(getCollectionPlural() + objectSetID + "/metadata/").get(PaginatedResult.class);
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

    /**
     * Adds objects to a given object set based on a user-provided query. The objectSetId and query are passed to a call
     * to {@link org.openskye.resource.ObjectSetResource#addFromSearch(String, String)}.
     */
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

    /**
     * Adds a specific object to an object set. The metadata id of the object to add is provided in the command, as well
     * as the id of object set to add the object to. Those ids are passed to a cal to {@link org.openskye.resource.ObjectSetResource#addObject(String, String)}.
     */
    public void add() {
        String objectSetID = dynamicParams.get("objectSetId");
        String objectMetadataId = dynamicParams.get("objectId");

        getResource(getCollectionPlural() + "/" + objectSetID + "/" + objectMetadataId).put();
    }


}
