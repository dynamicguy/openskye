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
import org.openskye.domain.Project;
import org.openskye.domain.Task;
import org.openskye.domain.dao.PaginatedResult;
import org.openskye.task.step.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A set of commands that manage batch mode processing steps in Skye ({@link Task}s), represented by {@link TaskStep}
 * subclasses.
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public class TasksCommand extends ExecutableCommand {

    private final String commandName = "tasks";
    /**
     * A JCommand parameter which represents a list request. {@link #list()} is called if this parameter is set to true
     * (set by the user by adding --list to the end of their command).
     */
    @Parameter(names = "--list")
    protected boolean list;
    /**
     * A JCommand parameter which represents a command to create a Discover task. {@link #discover()} is called if this
     * parameter is set to true (set by the user by adding --discover to the end of their command).
     */
    @Parameter(names = "--discover")
    protected boolean discover;
    /**
     * A JCommand parameter which represents a command to create an Archive task. {@link #archive()} is called if this
     * parameter is set to true (set by the user by adding --archive to the end of their command).
     */
    @Parameter(names = "--archive")
    protected boolean archive;
    /**
     * A JCommand parameter which represents a command to create a Cull task. {@link #cull()} is called if this
     * parameter is set to true (set by the user by adding --cull to the end of their command).
     */
    @Parameter(names = "--cull")
    protected boolean cull;
    /**
     * A JCommand parameter which represents a command to create a Classify task. {@link #classify()} is called if this
     * parameter is set to true (set by the user by adding --classify to the end of their command).
     */
    @Parameter(names = "--classify")
    protected boolean classify;
    @Parameter(names = "--verify")
    protected boolean verify;
    /**
     * A JCommand parameter which represents a command to create an Extraction task. {@link #extract()} is called if
     * this parameter is set to true (set by the user by adding --extract to the end of their command).
     */
    @Parameter(names = "--extract")
    protected boolean extract;
    /**
     * A JCommand parameter which represents a command to create a Destruction task. {@link #destroy()} is called if
     * this parameter is set to true (set by the user by adding --destroy to the end of their command).
     */
    @Parameter(names = "--destroy")
    protected boolean destroy;
    /**
     * A JCommand parameter which represents a command to create a test task. {@link #test()} is called if this
     * parameter is set to true (set by the user by adding --test to the end of their command).
     */
    @Parameter(names = "--test")
    protected boolean test;

    /**
     * Returns the Java {@link Class} that this command corresponds to
     *
     * @return the commands' {@link Class}
     */
    public Class getClazz() {
        return Task.class;
    }

    /**
     * Returns class name in lower camel case, singular form.
     *
     * @return the converted class name
     */
    public String getCollectionSingular() {
        return toCamel(getClazz().getSimpleName());
    }

    /**
     * Returns class name in lower camel case, plural form. Used most often to create REST requests
     *
     * @return the converted class name
     */
    public String getCollectionPlural() {
        return toCamel(getClazz().getSimpleName() + "s");
    }

    /**
     * Returns the fields required for Tasks
     *
     * @return a list of {@link Field}s
     */
    public List<Field> getFields() {
        return FieldBuilder.start().add(new TextField("workerName")).add(new TextField("projectId")).build();
    }

    /**
     * Creates a new {@link org.openskye.domain.Task} based on a given {@link TaskStep}.
     *
     * @param step the TaskStep to create a Task from. Depending on the type of TaskStep, different Tasks are created.
     */
    public void create(TaskStep step) {
        output.message("Creating a new " + step.getLabel() + " task:\n");
        String apiDirect = "/" + step.getLabel().toLowerCase();
        Task result = (Task) getResource(getCollectionPlural() + apiDirect).post(getClazz(), step);
        saveAlias(result.getId());
        output.success(step.getLabel() + " task started at " + result.getStarted());
    }

    /**
     * Parses a tasks command sent from the command line, and runs the task type based on the user parameters.
     */
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
        } else if (extract) {
            extract();
        } else if (destroy) {
            destroy();
        } else if (test) {
            test();
        }
    }

    /**
     * Lists all tasks.
     */
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

    /**
     * Creates a new Discovery task. Builds a {@link DiscoverTaskStep} based on information entered to the command line
     * by the user and runs {@link #create(org.openskye.task.step.TaskStep)} based on the new TaskStep.
     */
    public void discover() {
        TaskStep step = new DiscoverTaskStep();
        output.message("Creating a new " + step.getLabel() + " task:\n");
        step = (DiscoverTaskStep) selectReferenceField(new ReferenceField(Channel.class), step);
        create(step);
    }

    /**
     * Creates a new Archive task. Builds a {@link ArchiveTaskStep} based on information entered to the command line by
     * the user and runs {@link #create(org.openskye.task.step.TaskStep)} based on the new TaskStep.
     */
    public void archive() {
        TaskStep step = new ArchiveTaskStep();
        output.message("Creating a new " + step.getLabel() + " task:\n");
        step = (ArchiveTaskStep) selectReferenceField(new ReferenceField(Channel.class), step);
        create(step);
    }

    /**
     * Creates a new Cull task. Builds a {@link CullTaskStep} based on information entered to the command line by the
     * user and runs {@link #create(org.openskye.task.step.TaskStep)} based on the new TaskStep.
     */
    public void cull() {
        TaskStep step = new CullTaskStep();
        output.message("Creating a new " + step.getLabel() + " task:\n");
        step = (CullTaskStep) selectReferenceField(new ReferenceField(Project.class), step);
        create(step);
    }

    /**
     * Creates a new Classify task. Builds a {@link ClassifyTaskStep} based on information entered to the command line
     * by the user and runs {@link #create(org.openskye.task.step.TaskStep)} based on the new TaskStep.
     */
    public void classify() {
        TaskStep step = new ClassifyTaskStep();
        output.message("Creating a new " + step.getLabel() + " task:\n");
        step = (ClassifyTaskStep) selectReferenceField(new ReferenceField(Project.class), step);
        create(step);
    }

    public void verify() {
        TaskStep step = new VerifyTaskStep();
        output.message("Creating a new " + step.getLabel() + " task:\n");
        //TODO verify task step still under construction
        create(step);
    }

    /**
     * Creates a new Extract task. Builds a {@link ExtractTaskStep} based on information entered to the command line by
     * the user and runs {@link #create(org.openskye.task.step.TaskStep)} based on the new TaskStep.
     */
    public void extract() {
        TaskStep step = new ExtractTaskStep();
        output.message("Creating a new " + step.getLabel() + " task:\n");
        if (dynamicParams.get("objectSetId") != null) {
            ((ExtractTaskStep) step).setObjectSetId(resolveAlias(dynamicParams.get("objectSetId")));
        } else {
            step = (ExtractTaskStep) selectReferenceField(new ReferenceField(Channel.class), step);
        }
        step = setTargetInformationStore(step);
        create(step);
    }

    /**
     * Creates a new Destroy task. Builds a {@link DestroyTaskStep} based on information entered to the command line by
     * the user and runs {@link #create(org.openskye.task.step.TaskStep)} based on the new TaskStep.
     */
    public void destroy() {
        TaskStep step = new DestroyTaskStep();
        output.message("Creating a new " + step.getLabel() + " task:\n");
        ((DestroyTaskStep) step).setObjectSetId(resolveAlias(dynamicParams.get("objectSetId")));
        step = setTargetInformationStore(step);
        create(step);
    }

    /**
     * Creates a new Test task. Builds a {@link TestTaskStep} based on information entered to the command line by the
     * user and runs {@link #create(org.openskye.task.step.TaskStep)} based on the new TaskStep.
     */
    public void test() {
        TaskStep step = new TestTaskStep();
        selectReferenceField(new ReferenceField(Project.class), step);
        enterNumber(new NumberField("sleepSeconds"), step);
        enterNumber(new NumberField("iterations"), step);
        enterBoolean(new BooleanField("pass"), step);
    }

    /**
     * Sets the target {@link InformationStoreDefinition} for the TaskSteps that require it (namely {@link
     * ExtractTaskStep} and {@link DestroyTaskStep})
     *
     * @param step the {@link TaskStep} that requires the target information store to be set
     *
     * @return the newly modified {@link TaskStep}
     */
    public TaskStep setTargetInformationStore(TaskStep step) {

        try {
            InformationStoreDefinition chosenDef = getResource("informationStoreDefinitions/" + resolveAlias(dynamicParams.get("targetInformationStoreDefinition"))).get(InformationStoreDefinition.class);
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
