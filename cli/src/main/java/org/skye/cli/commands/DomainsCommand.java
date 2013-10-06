package org.skye.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.common.collect.ImmutableList;
import com.sun.jersey.api.client.GenericType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.skye.cli.CliException;
import org.skye.cli.util.ObjectTableView;
import org.skye.domain.Domain;
import org.skye.domain.dao.PaginatedResult;

import java.io.Console;

/**
 * The login command
 */
@Parameters(commandDescription = "Manage domains", separators = ":")
@Data
@Slf4j
public class DomainsCommand extends ExecutableCommand {

    private final String commandName = "domains";
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

    @Override
    public void execute() {

        // Ensure we are logged in
        settings.mustHaveApiKey();

        if (list) {
            PaginatedResult<Domain> paginatedResult = getResource("domains").get(new GenericType<PaginatedResult<Domain>>() {
            });
            consoleLogger.message("Found " + paginatedResult.getResults().size() + " domain(s)\n\n");
            ObjectTableView tableView = new ObjectTableView(paginatedResult, ImmutableList.of("id", "name"));
            tableView.draw(consoleLogger);
        } else if (create) {
            Domain newDomain = new Domain();
            Console console = getConsole();
            newDomain.setName(console.readLine("Domain name:"));
            consoleLogger.message("Creating");
            Domain result = getResource("domains").post(Domain.class, newDomain);
            consoleLogger.success("Created domain " + result);
        } else if (delete) {
            if (id == null)
                throw new CliException("You must provide an id to delete a domain");
            getResource("domains/" + id).delete();
        }
    }
}
