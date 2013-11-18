package org.openskye.cli.commands;

import com.beust.jcommander.Parameter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.openskye.cli.commands.fields.*;
import org.openskye.cli.util.ObjectTableView;
import org.openskye.core.ObjectSet;
import org.openskye.core.SkyeException;
import org.openskye.domain.Channel;
import org.openskye.domain.Project;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.task.step.*;

import java.util.ArrayList;
import java.util.List;

/**
 * An Abstract Base for commands that manage batch mode
 * processing steps in Skye, represented by TaskStep subclasses
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractTaskStepCommand extends ExecutableCommand {

    private final String commandName = "tasks";

    @Parameter(names = "--list")
    protected boolean list;
    @Parameter(names = "--discover")
    protected boolean discover;
    @Parameter(names = "--archive")
    protected boolean archive;
    @Parameter(names = "--verify")
    protected boolean verify;
    @Parameter(names = "--extract")
    protected boolean extract;
    @Parameter(names = "--destroy")
    protected boolean destroy;
    @Parameter(names = "--test")
    protected boolean test;

    public abstract Class getClazz();

    public String getCollectionSingular() {
        return toCamel(getClazz().getSimpleName());
    }

    public String getCollectionPlural() {
        return toCamel(getClazz().getSimpleName() + "s");
    }

    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("workerName")).add(new TextField("projectId")).build();
    }

    protected abstract void create(TaskStep step);

    @Override
    public void execute() {
        // Ensure we are logged in
        settings.mustHaveApiKey();
        if (list) {
            list();
        } else if (discover) {
            discover();
        } else if (archive) {
            archive();
        } else if (verify) {
            verify();
        } else if (extract) {
            extract();
        } else if (destroy) {
            destroy();
        } else if (test) {
            test();
        }
    }

    public void list() {
        PaginatedResult paginatedResult = getResource(getCollectionPlural()).get(PaginatedResult.class);
        List<String> fieldsWithId = new ArrayList<>();
        fieldsWithId.add("id");
        fieldsWithId.addAll(getFieldNames());
        if (paginatedResult.getResults().size() > 0) {
            output.message("Listing " + getCollectionPlural());
            ObjectTableView tableView = new ObjectTableView(paginatedResult, fieldsWithId);
            output.insertLines(1);
            tableView.draw(output);
            output.success("\nFound " + paginatedResult.getResults().size() + " " + getCollectionPlural());
        } else {
            output.success("\nNo " + getCollectionPlural() + " found");
        }
    }

    public void discover() {
        TaskStep step = new DiscoverTaskStep();
        selectReferenceField(new ReferenceField(Channel.class),step);
        create(step);
    }

    public void archive() {
        TaskStep step = new ArchiveTaskStep();
        selectReferenceField(new ReferenceField(Channel.class),step);
        create(step);
    }

    public void verify() {
        TaskStep step = new VerifyTaskStep();
        //TODO verify task step still under construction
        create(step);
    }

    public void extract() {
        TaskStep step = new ExtractTaskStep();
        //TODO extract should optionally take a set ID parameter instead of a channel
        selectReferenceField(new ReferenceField(Channel.class),step);
        create(step);
    }

    public void destroy() {
        TaskStep step = new DestroyTaskStep();
        selectReferenceField(new ReferenceField(ObjectSet.class),step);
        create(step);
    }

    public void test() {
        TaskStep step = new TestTaskStep();
        selectReferenceField(new ReferenceField(Project.class),step);
        enterNumber(new NumberField("sleepSeconds"),step);
        enterNumber(new NumberField("iterations"),step);
        enterBoolean(new BooleanField("pass"),step);
    }
}
