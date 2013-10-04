package org.skye.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.sun.jersey.api.client.GenericType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.skye.domain.Domain;
import org.skye.domain.dao.PaginatedResult;

/**
 * The login command
 */
@Parameters(commandDescription = "Login to the server")
@Data
@Slf4j
public class DomainsCommand extends ExecutableCommand {

    private final String name = "domains";
    @Parameter(names = "--list")
    private boolean list;
    @Parameter(names = "--delete")
    private boolean delete;
    @Parameter(names = "--create")
    private boolean create;

    @Override
    public void execute() {
        if (list) {
            PaginatedResult<Domain> paginatedResult = getResource("domains").get(new GenericType<PaginatedResult<Domain>>() {
            });

            DomainsCommand.log.info(paginatedResult.toString());
            // Need a nice way to display a table of results?
        } else if (create) {

        } else if (delete) {

        }
    }
}
