package org.skye.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.sun.jersey.api.client.GenericType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.skye.cli.CliException;
import org.skye.domain.Domain;
import org.skye.domain.dao.PaginatedResult;

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
    private String file;
    @Parameter(names = "--name")
    private String name;
    @Parameter(names = "--id")
    private String id;

    @Override
    public void execute() {

        // Ensure we are logged in
        settings.mustHaveApiKey();

        if (list) {
            PaginatedResult<Domain> paginatedResult = getResource("domains").get(new GenericType<PaginatedResult<Domain>>() {
            });

            consoleLogger.message(paginatedResult.toString());
            // Need a nice way to display a table of results?
        } else if (create) {
            Domain newDomain = new Domain();
            newDomain.setName(name);
            getResource("domains").post(newDomain);
        } else if (delete) {
            if (id == null)
                throw new CliException("You must provide an id to delete a domain");
            getResource("domains/" + id).delete();
        }
    }
}
