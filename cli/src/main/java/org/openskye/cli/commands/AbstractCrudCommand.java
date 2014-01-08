package org.openskye.cli.commands;

import com.beust.jcommander.Parameter;
import com.google.common.base.CaseFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.openskye.cli.CliException;
import org.openskye.cli.commands.fields.*;
import org.openskye.cli.util.ObjectTableView;
import org.openskye.core.ObjectSet;
import org.openskye.core.SkyeException;
import org.openskye.domain.Identifiable;
import org.openskye.domain.dao.PaginatedResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * An Abstract Base for Crud-like commands which are in place for setting up the metadata for Skye processing. Each
 * implementing class provides access to a specific API endpoint.
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractCrudCommand extends ExecutableCommand {


    /**
     * A JCommand parameter which represents a list request. {@link #list()} is called if this parameter is set to true
     * (set by the user by adding --list to the end of their command).
     */
    @Parameter(names = "--list")
    protected boolean list;
    /**
     * A JCommand parameter which represents a delete request. {@link #delete()} is called if this parameter is set to
     * true (set by the user by adding --delete to the end of their command).
     */
    @Parameter(names = "--delete")
    protected boolean delete;
    /**
     * A JCommand parameter which represents a creation request. {@link #create()} is called if this parameter is set to
     * true (set by the user by adding --create to the end of their command).
     */
    @Parameter(names = "--create")
    protected boolean create;
    /**
     * A JCommand parameter which represents a get request. {@link #get()} is called if this parameter is set to true
     * (set by the user by adding --get to the end of their command).
     */
    @Parameter(names = "--get")
    protected boolean get;
    @Parameter
    private List<String> id;

    public abstract List<Field> getFields();

    /**
     * Returns the Java {@link Class} that this command corresponds to
     *
     * @return the commands' {@link Class}
     */
    public abstract Class getClazz();

    @Override
    public void execute() {
        // Ensure we are logged in
        settings.mustHaveApiKey();

        if (list) {
            list();

        } else if (create) {
            create();
        } else if (delete) {
            delete();
        } else if (get) {
            get();
        }

    }

    public Collection<? extends String> getFieldNames() {
        List<String> fieldNames = new ArrayList();
        for (Field field : getFields()) {
            fieldNames.add(field.getName());
        }
        return fieldNames;
    }

    protected String toCamel(String name) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name);
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
     * Lists all instances for this endpoint object. A <code>getAll()</code> request is performed on the API, and the
     * paginated result is returned in a tabular structure.
     */
    public void list() {
        PaginatedResult paginatedResult = getResource(getCollectionPlural()).get(PaginatedResult.class);
        List<String> fieldsWithId = new ArrayList<>();
        fieldsWithId.add("id");
        fieldsWithId.addAll(getFieldNames());

        if (paginatedResult.getResults().size() > 0) {
            // If an alias was specified on the command line, and the listed objects are identifiable
            // save the id of the first listed result to the alias
            Object firstResult = paginatedResult.getResults().get(0);
            if ( firstResult instanceof Map ) {
                Map<String,String> firstMap = (Map<String,String>) firstResult;
                if ( firstMap.containsKey("id") ) {
                  saveAlias(firstMap.get("id"));
                }
            }

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
     * Gets a specific instance for this endpoint object. A <code>get()</code> request is performed on the API, and the
     * result is returned.
     */
    private void get() {
        String id = dynamicParams.get("id");
        if (id == null) {
            output.error("You must enter an id");
        } else {
            Object result = getResource(getCollectionPlural() + "/" + id).get(getClazz());
            //TODO: Print this result in a nicer way
            output.raw(result.toString());
        }
    }

    /**
     * Creates a new instance of this endpoint object based on the combination of parameters and dynamic parameters
     * entered to the command line.
     */
    public void create() {
        output.message("Creating a new " + getCollectionSingular() + ":\n");
        Object newObject;
        try {
            newObject = getClazz().newInstance();
        } catch (Exception e) {
            throw new SkyeException("Unable to create instance of " + getClazz(), e);
        }
        for (Field field : getFields()) {
            String attributeName = field.getName();
            String attributeVal = dynamicParams.get(attributeName);
            if (field instanceof TextField) {
                String newValue = attributeVal;
                try {
                    BeanUtils.setProperty(newObject, attributeName, newValue);
                    output.raw(newObject.toString());
                } catch (Exception e) {
                    throw new SkyeException("Unable to set property " + attributeName + " on " + newObject + " to " + newValue);
                }
            } else if (field instanceof ReferenceField) {
                newObject = selectReferenceField((ReferenceField) field, newObject);
            } else if (field instanceof PropertiesField) {
                newObject = setPropertiesField((PropertiesField) field, newObject);
            } else if (field instanceof EnumerationField) {
                selectEnum((EnumerationField) field, newObject);
            }
        }
        Object result = getResource(getCollectionPlural()).post(getClazz(), newObject);
        String id;
        if ( result instanceof Identifiable ) {
            id = ((Identifiable) result).getId();
        } else if ( result instanceof ObjectSet ) {
            id = ((ObjectSet) result).getId();
        } else {
            throw new SkyeException("Cannot resolve id for "+result.getClass().getName());
        }
        saveAlias(id);
        output.success("Created " + getCollectionSingular() + " with id " + id);
    }

    /**
     * Deletes an instance of this endpoint object
     */
    public void delete() {
        String id = dynamicParams.get("id");
        if (id == null)
            throw new CliException("You must provide an id to delete a " + getCollectionSingular());

        else {
            getResource(getCollectionPlural() + "/" + id).delete();

            output.success("Deleted " + getCollectionSingular() + " with id " + id);
        }
    }
}
