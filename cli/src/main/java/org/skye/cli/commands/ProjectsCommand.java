package org.skye.cli.commands;

import com.beust.jcommander.Parameters;
import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * The login command
 */
@Parameters(commandDescription = "Manage projects")
@Data
@Slf4j
public class ProjectsCommand extends AbstractCrudCommand {

    private final String commandName = "projects";

    public List<String> getAttributes() {
        return ImmutableList.of("name");
    }

    public String getCollectionName() {
        return "projects";
    }

    public String getCollectionSingular() {
        return "project";
    }

    public String getCollectionPlural() {
        return "projects";
    }

}
