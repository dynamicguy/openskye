package org.openskye.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.inject.Inject;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.*;
import org.openskye.cli.util.ObjectTableView;
import org.openskye.core.ObjectMetadata;
import org.openskye.core.SkyeException;
import org.openskye.domain.Project;
import org.openskye.domain.dao.PaginatedResult;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
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
    private final String pageParam = "_page";
    private final String pageSizeParam = "_pageSize";

    @Inject
    private final String commandName = "objects";

    @Parameter(names = "--search")
    private boolean search;

    @Parameter(names = "--searchCount")
    private boolean count;

    @Override
    public void execute() {
        super.execute();

        if (search) {
            search();
        }

        if (count) {
            count();
        }
    }

    @Override
    public List<Field> getFields() {
        return FieldBuilder.start().add(new NumberField("archiveSize")).add(new NumberField("checksum")).add(new BooleanField("container")).add(new TextField("path")).add(new TextField("implementation")).add(new TextField("informationStoreId")).add(new TextField("mimeType")).add(new ReferenceField(Project.class)).add(new PropertiesField("metadata")).build();
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

    public void search() {
        String query = dynamicParams.get("query");
        String strPage = dynamicParams.get(pageParam);
        String strPageSize = dynamicParams.get(pageSizeParam);
        String projectId = resolveAlias(dynamicParams.get("project"));

        if(strPageSize!=null) {
            try {
                Long.parseLong(strPageSize);
            } catch (Exception ex) {
                throw new SkyeException("The _pageSize parameter must be an integer", ex);
            }
        }

        if(strPage!=null) {
            try {
                Long.parseLong(strPage);
            } catch (Exception ex) {
                throw new SkyeException("The _page parameter must be an integer", ex);
            }
        }

        MultivaluedMap queryParams = new MultivaluedMapImpl();
        String url = settings.getUrl() + getCollectionPlural() + "/search";

        queryParams.add("query", query);
        queryParams.add("_page", strPage);
        queryParams.add("_pageSize", strPageSize);

        if (projectId != null && !projectId.isEmpty()) {
            url += "/" + projectId;
        }

        WebResource resource = client.resource(url).queryParams(queryParams);

        WebResource.Builder builder = resource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE);

        if (settings.getApiKey() != null) {
            builder.header("X-Api-Key", settings.getApiKey());
        }

        PaginatedResult paginatedResult = builder.get(PaginatedResult.class);

        List<String> fieldsWithId = new ArrayList<>();
        fieldsWithId.add("id");
        fieldsWithId.addAll(getFieldNames());

        if (paginatedResult.getTotalResults() > 0) {
            output.message("Listing " + getCollectionPlural());

            ObjectTableView tableView = new ObjectTableView(paginatedResult, fieldsWithId);
            output.insertLines(1);
            tableView.draw(output);

            output.success("\nFound " + paginatedResult.getTotalResults() + " " + getCollectionPlural());
            output.success("Showing page number: " + paginatedResult.getPage());
            output.success("Results on this page: " + paginatedResult.getResults().size());
        } else {
            output.success("\nNo " + getCollectionPlural() + " found");
        }
    }

    public void count() {
        String query = dynamicParams.get("query");
        String projectId = resolveAlias(dynamicParams.get("project"));

        MultivaluedMap queryParams = new MultivaluedMapImpl();
        String url = settings.getUrl() + getCollectionPlural() + "/search/count";

        queryParams.add("query", query);

        if (projectId != null && !projectId.isEmpty()) {
            url += "/" + projectId;
        }

        WebResource resource = client.resource(url).queryParams(queryParams);

        WebResource.Builder builder = resource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE);

        if (settings.getApiKey() != null) {
            builder.header("X-Api-Key", settings.getApiKey());
        }

        Long results = builder.get(Long.class);

        output.success("Showing number of results to the query: " + results);
    }

}
