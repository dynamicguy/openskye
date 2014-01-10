package org.openskye.cli.commands;

import com.beust.jcommander.Parameter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.openskye.cli.commands.fields.*;
import org.openskye.cli.util.ObjectTableView;
import org.openskye.core.SkyeException;
import org.openskye.domain.Channel;
import org.openskye.domain.InformationStoreDefinition;
import org.openskye.domain.Node;
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
    @Parameter(names = "--cull")
    protected boolean cull;
    @Parameter(names = "--classify")
    protected boolean replicate;
    @Parameter(names = "--replicate")
    protected boolean classify;
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
        return FieldBuilder.start().add(new TextField("projectId")).build();
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
        } else if (cull) {
            cull();
        } else if (classify) {
            classify();
        } else if (verify) {
            verify();
        } else if (replicate) {
            replicate();
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
        output.message("Creating a new " + step.getLabel() + " task:\n");
        selectReferenceField(new ReferenceField(Node.class), step);
        selectReferenceField(new ReferenceField(Channel.class), step);
        create(step);
    }

    public void archive() {
        TaskStep step = new ArchiveTaskStep();
        output.message("Creating a new " + step.getLabel() + " task:\n");
        selectReferenceField(new ReferenceField(Node.class), step);
        selectReferenceField(new ReferenceField(Channel.class), step);
        create(step);
    }

    public void cull() {
        TaskStep step = new CullTaskStep();
        output.message("Creating a new " + step.getLabel() + " task:\n");
        selectReferenceField(new ReferenceField(Node.class), step);
        selectReferenceField(new ReferenceField(Project.class), step);
        create(step);
    }

    public void replicate() {
        TaskStep step = new ReplicateTaskStep();
        output.message("Creating a new " + step.getLabel() + " task:\n");
        selectReferenceField(new ReferenceField(Node.class), step);
        selectReferenceField(new ReferenceField(Project.class), step);
        create(step);
    }

    public void classify() {
        TaskStep step = new ClassifyTaskStep();
        output.message("Creating a new " + step.getLabel() + " task:\n");
        selectReferenceField(new ReferenceField(Node.class), step);
        selectReferenceField(new ReferenceField(Project.class), step);
        create(step);
    }

    public void verify() {
        TaskStep step = new VerifyTaskStep();
        output.message("Creating a new " + step.getLabel() + " task:\n");
        selectReferenceField(new ReferenceField(Node.class), step);
        //TODO verify task step still under construction
        create(step);
    }

    public void extract() {
        TaskStep step = new ExtractTaskStep();
        output.message("Creating a new " + step.getLabel() + " task:\n");
        selectReferenceField(new ReferenceField(Node.class), step);
        //TODO extract should optionally take a set ID parameter instead of a channel
        if (dynamicParams.get("objectSetId") != null) {
            ((ExtractTaskStep) step).setObjectSetId(dynamicParams.get("objectSetId"));
        } else {
            selectReferenceField(new ReferenceField(Channel.class), step);
        }
        step = setTargetInformationStore(step);
        create(step);
    }

    public void destroy() {
        TaskStep step = new DestroyTaskStep();
        output.message("Creating a new " + step.getLabel() + " task:\n");
        step = (ReplicateTaskStep) selectReferenceField(new ReferenceField(Node.class), step);
        ((DestroyTaskStep) step).setObjectSetId(dynamicParams.get("objectSetId"));
        step = setTargetInformationStore(step);
        create(step);
    }

    public void test() {
        TaskStep step = new TestTaskStep();
        output.message("Creating a new " + step.getLabel() + " task:\n");
        selectReferenceField(new ReferenceField(Node.class), step);
        selectReferenceField(new ReferenceField(Project.class), step);
        enterNumber(new NumberField("sleepSeconds"), step);
        enterNumber(new NumberField("iterations"), step);
        enterBoolean(new BooleanField("pass"), step);
    }

    public TaskStep setTargetInformationStore(TaskStep step) {

        try {
            InformationStoreDefinition chosenDef = getResource("informationStores/" + dynamicParams.get("targetInformationStoreDefinition")).get(InformationStoreDefinition.class);
            if (step instanceof ExtractTaskStep) {
                ((ExtractTaskStep) step).setTargetInformationStoreDefinition(chosenDef);
            } else {
                ((DestroyTaskStep) step).setTargetInformationStoreDefinition(chosenDef);
            }

        } catch (Exception e) {
            throw new SkyeException("Could not assign information store", e);
        }
        return step;
    }


}
