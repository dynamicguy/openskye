package org.openskye.cli.commands;

import com.beust.jcommander.Parameter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.openskye.cli.CliException;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.ReferenceField;
import org.openskye.cli.commands.fields.TextField;
import org.openskye.cli.util.ObjectTableView;
import org.openskye.core.SkyeException;
import org.openskye.domain.Identifiable;
import org.openskye.domain.dao.PaginatedResult;

import java.io.Console;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An Abstract Base for Crud-like commands which are in
 * place for setting up the metadata for Skye processing
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractCrudCommand extends ExecutableCommand {

    @Parameter(names = "--list")
    private boolean list;
    @Parameter(names = "--delete")
    private boolean delete;
    @Parameter(names = "--create")
    private boolean create;
    @Parameter(names = "--file")
    private String name;
    @Parameter
    private String id;

    public abstract List<Field> getFields();

    public abstract String getCollectionName();

    public abstract String getCollectionSingular();

    public abstract String getCollectionPlural();

    public abstract Class getClazz();

    @Override
    public void execute() {
        // Ensure we are logged in
        settings.mustHaveApiKey();

        if (list) {
            PaginatedResult paginatedResult = getResource(getCollectionName()).get(PaginatedResult.class);
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

        } else if (create) {
            output.message("Creating a new " + getCollectionSingular() + ":\n");
            Object newObject = null;
            try {
                newObject = getClazz().newInstance();
            } catch (Exception e) {
                throw new SkyeException("Unable to create instance of " + getClazz(), e);
            }
            Console console = getConsole();
            for (Field field : getFields()) {
                String attributeName = field.getName();
                if (field instanceof TextField) {
                    String newValue = console.readLine(StringUtils.capitalize(attributeName) + ": ");
                    try {
                        BeanUtils.setProperty(newObject, attributeName, newValue);
                    } catch (Exception e) {
                        throw new SkyeException("Unable to set property " + attributeName + " on " + newObject + " to " + newValue);
                    }
                } else if (field instanceof ReferenceField) {
                    selectReferenceField((ReferenceField) field, newObject);
                }
            }
            Identifiable result = (Identifiable) getResource(getCollectionName()).post(getClazz(), newObject);
            output.success("Created " + getCollectionSingular() + " with id " + result.getId());
        } else if (delete) {
            if (id == null)
                throw new CliException("You must provide an id to delete a " + getCollectionSingular());
            getResource(getCollectionPlural() + "/" + id).delete();

            output.success("Deleted " + getCollectionSingular() + " with id " + id);

        }
    }

    private void selectReferenceField(ReferenceField field, Object newObject) {

        // We need to display a list of the available options for the reference field
        // and then let the user choose one

        PaginatedResult paginatedResult = getResource(field.getResource()).get(PaginatedResult.class);

        output.message("Select " + field.getName() + " by number,  the options are below:");
        int i = 1;
        for (Object obj : paginatedResult.getResults()) {
            try {
                output.raw(" " + i + "/ " + BeanUtils.getProperty(obj, field.getValue()));
            } catch (Exception e) {
                throw new SkyeException("Unable to build reference field for " + field, e);
            }
            i++;
        }
        while (true) {
            String option = getConsole().readLine("Enter choice:");
            int position = Integer.parseInt(option);
            try {
                Object result = Class.forName(field.getClazz().getCanonicalName()).newInstance();
                BeanUtils.setProperty(result, "id", BeanUtils.getProperty(paginatedResult.getResults().get(position - 1), field.getId()));
                BeanUtils.setProperty(newObject, field.getName(), result);
                break;
            } catch (Exception e) {
                throw new SkyeException("Unable to build reference field for " + field, e);
            }
        }
    }

    public Collection<? extends String> getFieldNames() {
        List<String> fieldNames = new ArrayList();
        for (Field field : getFields()) {
            fieldNames.add(field.getName());
        }
        return fieldNames;
    }
}
