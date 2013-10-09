package org.skye.cli.commands;

import com.beust.jcommander.Parameter;
import com.sun.jersey.api.client.GenericType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.skye.cli.CliException;
import org.skye.cli.util.ObjectTableView;
import org.skye.core.SkyeException;
import org.skye.domain.Domain;
import org.skye.domain.dao.PaginatedResult;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

/**
 * An Abstract Base for Crud-like commands which are in
 * place for setting up the metadata for Skye processing
 */
@Data
@Slf4j
public abstract class AbstractCrudCommand extends ExecutableCommand {

    @Parameter(names = "--list")
    private boolean list;
    @Parameter(names = "--delete")
    private boolean delete;
    @Parameter(names = "--create")
    private boolean create;
    @Parameter(names = "--file")
    private String name;
    @Parameter
    private String id;

    public abstract List<String> getAttributes();

    public abstract String getCollectionName();

    public abstract String getCollectionSingular();

    public abstract String getCollectionPlural();

    @Override
    public void execute() {
        // Ensure we are logged in
        settings.mustHaveApiKey();

        if (list) {
            PaginatedResult<Domain> paginatedResult = getResource(getCollectionName()).get(new GenericType<PaginatedResult<Domain>>() {
            });
            List<String> attributesWithId = new ArrayList<>();
            attributesWithId.add("id");
            attributesWithId.addAll(getAttributes());

            if (paginatedResult.getResults().size() > 0) {
                output.message("Listing " + getCollectionPlural());

                ObjectTableView tableView = new ObjectTableView(paginatedResult, attributesWithId);
                output.insertLines(1);
                tableView.draw(output);
                output.success("\nFound " + paginatedResult.getResults().size() + " " + getCollectionPlural());

            } else {
                output.success("\nNo " + getCollectionPlural() + " found");

            }

        } else if (create) {
            output.message("Creating a new " + getCollectionSingular() + ":\n");
            Domain newDomain = new Domain();
            Console console = getConsole();
            for (String attribute : getAttributes()) {
                String newValue = console.readLine(StringUtils.capitalize(attribute) + ": ");
                try {
                    BeanUtils.setProperty(newDomain, attribute, newValue);
                } catch (Exception e) {
                    throw new SkyeException("Unable to set property " + attribute + " on " + newDomain + " to " + newValue);
                }
            }
            Domain result = getResource(getCollectionName()).post(Domain.class, newDomain);
            output.success("Created " + getCollectionSingular() + " with id " + result.getId());
        } else if (delete) {
            if (id == null)
                throw new CliException("You must provide an id to delete a " + getCollectionSingular());
            getResource("domains/" + id).delete();

            output.success("Deleted " + getCollectionSingular() + " with id " + id);

        }
    }
}