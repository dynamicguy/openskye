package org.openskye.cli.commands;

import com.beust.jcommander.Parameter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.FieldBuilder;
import org.openskye.cli.commands.fields.ReferenceField;
import org.openskye.cli.commands.fields.TextField;
import org.openskye.cli.util.ObjectTableView;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.domain.Project;
import org.openskye.domain.TaskLog;
import org.openskye.domain.dao.PaginatedResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Traght on 1/20/14.
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class TaskLogsCommand extends ExecutableCommand {

    private final String commandName = "taskLogs";

    @Parameter(names = "--list")
    protected String taskId = null;

    @Parameter(names = "--get")
    protected boolean get;
    @Parameter
    private List<String> id;

    @Override
    public void execute() {
        // Ensure we are logged in
        settings.mustHaveApiKey();

        if (taskId != null) {
            list(resolveAlias(taskId));
        } else if (get) {
            for ( String logId : id ) {
                get(resolveAlias(logId));
            }
        }
    }

    public void list(String taskId) {
        PaginatedResult paginatedResult = getResource("tasks/"+taskId+"/taskLogs").get(PaginatedResult.class);
        List<String> displayFields = new ArrayList<>();
        displayFields.add("id");
        displayFields.add("status");
        displayFields.add("logTime");
        displayFields.add("message");

        if (paginatedResult.getResults().size() > 0) {
            // If an alias was specified on the command line, and the listed objects are identifiable
            // save the id of the first listed result to the alias
            Object firstResult = paginatedResult.getResults().get(0);
            if ( firstResult instanceof Map) {
                Map<String,String> firstMap = (Map<String,String>) firstResult;
                if ( firstMap.containsKey("id") ) {
                    saveAlias(firstMap.get("id"));
                }
            }

            output.message("Listing taskLogs");

            ObjectTableView tableView = new ObjectTableView(paginatedResult, displayFields);
            output.insertLines(1);
            tableView.draw(output);
            output.success("\nFound " + paginatedResult.getResults().size() + " task logs");

        } else {
            output.success("\nNo task logs found");

        }
    }

    public void get(String logId) {

        if (id == null) {
            output.error("You must enter an id");
        } else {
            Object result = getResource("taskLogs/" + logId).get(TaskLog.class);
            PaginatedResult paginatedResult = new PaginatedResult(Arrays.asList(result));
            if ( paginatedResult.getTotalResults() == 0 ) {
                output.success("\nTask log "+logId+" found");
            } else {
                TaskLog taskLog = (TaskLog) paginatedResult.getResults().get(0);
                output.raw("\nTask Log Id: "+taskLog.getId());
                output.raw("\nTask Id: "+taskLog.getTaskId());
                output.raw("\nStatus: "+taskLog.getStatus());
                output.raw("\nDate/Time: "+taskLog.getLogTime());
                output.raw("\nMessage: "+taskLog.getMessage());
                printException(taskLog.getException());
                saveAlias(logId);
            }
        }
    }

    private void printException(Throwable e) {
        if ( e == null ) {
            return;
        } else {
            output.raw("\n"+e.getClass().getName()+": "+e.getMessage());
            for ( StackTraceElement te : e.getStackTrace() ) {
                output.raw("\n    "+te.toString());
            }
            printException(e.getCause());
        }
    }
}
