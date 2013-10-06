package org.skye.cli.util;

import org.apache.commons.lang.StringUtils;
import org.skye.cli.ConsoleLogger;
import org.skye.domain.dao.PaginatedResult;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility that allows us to view certain
 * attributes off a PaginatedResult of an
 * object in a table view
 */
public class ObjectTableView {

    private final PaginatedResult paginatedResult;
    private final List<String> attributes;
    private List<TableColumn> columns = new ArrayList<>();

    public ObjectTableView(PaginatedResult result, List<String> attributes) {
        this.paginatedResult = result;
        this.attributes = attributes;
    }

    public void draw(ConsoleLogger logger) {
        // Build up the columns
        for (String attribute : attributes) {
            columns.add(new TableColumn(attribute));
        }

        for (Object obj : paginatedResult.getResults()) {
            for (TableColumn col : columns) {
                col.parseObject(obj);
            }
        }

        // We have the columsn in place, lets build the table
        int tableWidth = 1;
        for (TableColumn col : columns) {
            tableWidth = tableWidth + col.getMaxLength() + 1;
        }

        // Draw header top
        logger.raw(StringUtils.repeat("-", tableWidth));
        // Draw header
        StringBuilder sb = new StringBuilder();
        sb.append("|");
        for (TableColumn col : columns) {
            sb.append(col.getColumnHeader());
            sb.append("|");
        }
        logger.raw(sb.toString());

        // Draw header bottom
        logger.raw(StringUtils.repeat("-", tableWidth));

        // Draw rows
        sb = new StringBuilder();
        for (int i = 0; i < paginatedResult.getResults().size(); i++) {
            sb.append("|");
            for (TableColumn col : columns) {
                sb.append(col.getRowValue(i));
                sb.append("|");
            }
        }
        logger.raw(sb.toString());

        // Close table
        logger.raw(StringUtils.repeat("-", tableWidth));
    }
}
