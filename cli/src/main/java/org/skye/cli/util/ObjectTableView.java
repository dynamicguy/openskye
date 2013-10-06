package org.skye.cli.util;

import org.skye.cli.ConsoleLogger;
import org.skye.domain.dao.PaginatedResult;

import java.util.List;

/**
 * A utility that allows us to view certain
 * attributes off a PaginatedResult of an
 * object in a table view
 */
public class ObjectTableView {

    private final PaginatedResult paginatedResult;
    private final List<String> attributes;

    public ObjectTableView(PaginatedResult result, List<String> attributes) {
        this.paginatedResult = result;
        this.attributes = attributes;
    }

    public void draw(ConsoleLogger logger) {

    }
}
