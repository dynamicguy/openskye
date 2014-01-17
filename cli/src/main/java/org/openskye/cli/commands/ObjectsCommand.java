package org.openskye.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.inject.Inject;
import com.sun.jersey.api.client.WebResource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.*;
import org.openskye.cli.util.ObjectTableView;
import org.openskye.core.ObjectMetadata;
import org.openskye.domain.Project;
import org.openskye.domain.dao.PaginatedResult;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * A command to manage {@link ObjectMetadata}. <br /> <b>Note:</b> ObjectMetadata cannot be directly created, so a call
 * to {@link #create} will result in an error message.
 */

@Parameters(commandDescription = "Manage Object Metadata")
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class ObjectsCommand extends AbstractCrudCommand {

    @Inject
    private final String commandName = "objects";

    @Parameter(names = "--search")
    private boolean search;

    @Override
    public void execute(){
        super.execute();
        if(search){
            search();
        }
    }

    @Override
    public List<Field> getFields() {
        return FieldBuilder.start().add(new NumberField("archiveSize")).add(new NumberField("checksum")).add(new BooleanField("container")).add(new TextField("created")).add(new TextField("path")).add(new TextField("implementation")).add(new TextField("informationStoreId")).add(new TextField("ingested")).add(new TextField("lastModified")).add(new TextField("mimeType")).add(new ReferenceField(Project.class)).add(new PropertiesField("metadata")).build();
    }

    @Override
    public Class getClazz() {
        return ObjectMetadata.class;
    }

    @Override
    public String getCollectionPlural() {
        return "objects";
    }

    @Override
    public void create() {
        output.error("You cannot directly create object metadata");
    }

    public void search(){
        String query = dynamicParams.get("query");

        WebResource resource = client.resource(settings.getUrl() + getCollectionPlural() + "/search").queryParam("query", query);
        WebResource.Builder builder = resource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE);
        if (settings.getApiKey() != null) {
            builder.header("X-Api-Key", settings.getApiKey());
        }

        PaginatedResult paginatedResult = builder.get(PaginatedResult.class);

        List<String> fieldsWithId = new ArrayList<>();
        fieldsWithId.add("id");
        fieldsWithId.addAll(getFieldNames());

        if (paginatedResult.getResults().size() > 0) {

            output.message("Listing " + getCollectionPlural());

            ObjectTableView tableView = new ObjectTableView(paginatedResult, fieldsWithId);
            output.insertLines(1);
            tableView.draw(output);
            output.success("\nFound " + paginatedResult.getResults().size() + " " + getCollectionPlural());

        } else {
            output.success("\nNo " + getCollectionPlural() + " found");

        }
    }

}
