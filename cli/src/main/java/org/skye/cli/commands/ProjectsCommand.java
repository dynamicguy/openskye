package org.skye.cli.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.sun.jersey.api.client.GenericType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.skye.domain.Project;
import org.skye.domain.dao.PaginatedResult;

/**
 * The login command
 */
@Parameters(commandDescription = "Manage projects")
@Data
@Slf4j
public class ProjectsCommand extends ExecutableCommand {

    private final String commandName = "projects";
    @Parameter(names = "--list")
    private boolean list;

    @Override
    public void execute() {
        if (list) {
            PaginatedResult<Project> paginatedResult = getResource("projects").get(new GenericType<PaginatedResult<Project>>() {
            });

            log.info(paginatedResult.toString());

            // Need a nice way to display a table of results?
        }
    }
}
