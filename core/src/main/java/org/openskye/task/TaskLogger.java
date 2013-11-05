package org.openskye.task;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.openskye.domain.Task;
import org.openskye.domain.TaskLog;
import org.openskye.domain.dao.TaskLogDAO;

/**
 * Used to log events and errors occurring for a single Task
 */
@Slf4j
public class TaskLogger {

    @Inject
    TaskLogDAO taskLogDAO;

    Task task;
    String marker;

    public TaskLogger(Task task) {
        this.task = task;
        this.marker = "Task "+task.getId();
    }

    private void writeTaskLog(String message) {
        TaskLog taskLog = new TaskLog();
        taskLog.setTaskId(task.getId());
        taskLog.setStatus(task.getStatus());
        taskLog.setMessage(message);
        taskLogDAO.create(taskLog);
    }

    public void info(String message) {
        log.info(marker,message);
        writeTaskLog(message);
    }

    public void debug(String message) {
        log.debug(marker,message);
    }

    public void error(String message) {
        log.error(marker,message);
        writeTaskLog(message);
    }

    public void error(String message,Exception e) {
        log.error(marker,message,e);
        writeTaskLog(message);
        writeTaskLog(e.getLocalizedMessage());
    }
}
