package org.skye.cli.commands;

import com.beust.jcommander.Parameters;
import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * The login command
 */
@Parameters(commandDescription = "Manage domains")
@Data
@Slf4j
public class DomainsCommand extends AbstractCrudCommand {

    private final String commandName = "domains";

    public List<String> getAttributes() {
        return ImmutableList.of("name");
    }

    public String getCollectionName() {
        return "domains";
    }

    public String getCollectionSingular() {
        return "domain";
    }

    public String getCollectionPlural() {
        return "domains";
    }

}
