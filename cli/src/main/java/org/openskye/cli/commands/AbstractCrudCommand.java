package org.openskye.cli.commands;

import com.beust.jcommander.Parameter;
import com.google.common.base.CaseFormat;
import com.sun.jersey.core.util.MultivaluedMapImpl;
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

import javax.ws.rs.core.MultivaluedMap;
import java.util.*;

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
    /**
     * A JCommand parameter which represents an update request. {@link #update()} is called if this parameter is set to
     * true (set by the user by adding --get to the end of their command).
     */
    @Parameter(names = "--update")
    protected boolean update;
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
        } else if (update) {
            update();
        }

    }

    protected void update() {
        String id = resolveAlias(dynamicParams.get("id"));
        List<Field> fields = getFields();
        Object objectToChange = getResource(getCollectionPlural() + "/" + id).get(getClazz());
        String changes = dynamicParams.get("fields");
        String[] fieldPairs = changes.split(",");
        Map<String, String> fieldPairMap = new HashMap<>();
        //split and organize the fields to change
        for (String pairs : fieldPairs) {
            String[] pairSplit = pairs.split("::");
            fieldPairMap.put(pairSplit[0], pairSplit[1]);
        }

        for (Field field : fields) {
            if (fieldPairMap.containsKey(field.getName())) {
                String attributeVal = fieldPairMap.get(field.getName());
                String attributeName = field.getName();
                if (field instanceof TextField) {
                    String newValue = attributeVal;
                    try {
                        BeanUtils.setProperty(objectToChange, attributeName, newValue);
                        output.raw(objectToChange.toString());
                    } catch (Exception e) {
                        throw new SkyeException("Unable to set property " + attributeName + " on " + objectToChange + " to " + newValue);
                    }
                } else if (field instanceof NumberField) {
                    long newValue = Long.parseLong(attributeVal);
                    try {
                        BeanUtils.setProperty(objectToChange, attributeName, newValue);
                        output.raw(objectToChange.toString());
                    } catch (Exception e) {
                        throw new SkyeException("Unable to set property " + attributeName + " on " + objectToChange + " to " + newValue);
                    }
                } else if (field instanceof ReferenceField) {
                    objectToChange = selectReferenceField((ReferenceField) field, objectToChange);
                } else if (field instanceof PropertiesField) {
                    objectToChange = setPropertiesField((PropertiesField) field, objectToChange);
                } else if (field instanceof EnumerationField) {
                    selectEnum((EnumerationField) field, objectToChange);
                } else if (field instanceof NodeRolesField) {
                    objectToChange = setNodeRolesField((NodeRolesField) field, objectToChange);
                }
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
        final String pageParam = "_page";
        final String pageSizeParam = "_pageSize";
        final String sortParam = "_sort";
        final String sortDirParam = "_sortDir";

        String strPage = dynamicParams.get(pageParam);
        String strPageSize = dynamicParams.get(pageSizeParam);
        String strSort = dynamicParams.get(sortParam);
        String strSortDir = dynamicParams.get(sortDirParam);

        if (strPageSize != null) {
            try {
                Long.parseLong(strPageSize);
            } catch (Exception ex) {
                throw new SkyeException("The _pageSize parameter must be an integer", ex);
            }
        }

        if (strPage != null) {
            try {
                Long.parseLong(strPage);
            } catch (Exception ex) {
                throw new SkyeException("The _page parameter must be an integer", ex);
            }
        }

        MultivaluedMap queryParams = new MultivaluedMapImpl();

        queryParams.add(sortParam, strSort);
        queryParams.add(sortDirParam, strSortDir);
        queryParams.add(pageParam, strPage);
        queryParams.add(pageSizeParam, strPageSize);

        PaginatedResult paginatedResult = getResource(getCollectionPlural(), queryParams).get(PaginatedResult.class);
        List<String> fieldsWithId = new ArrayList<>();
        fieldsWithId.add("id");
        fieldsWithId.addAll(getFieldNames());

        if (paginatedResult.getResults().size() > 0) {
            // If an alias was specified on the command line, and the listed objects are identifiable
            // save the id of the first listed result to the alias
            Object firstResult = paginatedResult.getResults().get(0);
            if (firstResult instanceof Map) {
                Map<String, String> firstMap = (Map<String, String>) firstResult;
                if (firstMap.containsKey("id")) {
                    saveAlias(firstMap.get("id"));
                }
            }

            output.message("Listing " + getCollectionPlural());

            ObjectTableView tableView = new ObjectTableView(paginatedResult, fieldsWithId);
            output.insertLines(1);
            tableView.draw(output);
            output.success("\nFound " + paginatedResult.getTotalResults() + " " + getCollectionPlural());
            output.success("Showing page number: " + paginatedResult.getPage());
            output.success("Results on this page: " + paginatedResult.getResults().size());

        } else {
            output.success("\nNo " + getCollectionPlural() + " found");
        }
    }

    /**
     * Gets a specific instance for this endpoint object. A <code>get()</code> request is performed on the API, and the
     * result is returned.
     */
    private void get() {
        String id = resolveAlias(dynamicParams.get("id"));
        if (id == null) {
            output.error("You must enter an id");
        } else {
            Object result = getResource(getCollectionPlural() + "/" + id).get(getClazz());
            PaginatedResult paginatedResult = new PaginatedResult(Arrays.asList(result));
            List<String> fieldsWithId = new ArrayList<>();
            fieldsWithId.add("id");
            fieldsWithId.addAll(getFieldNames());
            ObjectTableView tableView = new ObjectTableView(paginatedResult, fieldsWithId);
            output.insertLines(1);
            tableView.draw(output);
            output.success("\nFound " + paginatedResult.getResults().size() + " " + getCollectionPlural());
            saveAlias(id);
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
            if (!field.isOptional && attributeVal == null) {
                throw new CliException("The required field " + attributeName + " is missing a value");
            } else if (attributeVal != null) {
                if (field instanceof TextField) {
                    String newValue = attributeVal;
                    try {
                        BeanUtils.setProperty(newObject, attributeName, newValue);
                        output.raw(newObject.toString());
                    } catch (Exception e) {
                        throw new SkyeException("Unable to set property " + attributeName + " on " + newObject + " to " + newValue);
                    }
                } else if (field instanceof NumberField) {
                    long newValue = Long.parseLong(attributeVal);
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
                } else if (field instanceof NodeRolesField) {
                    newObject = setNodeRolesField((NodeRolesField) field, newObject);
                }
            }
        }
        Object result = getResource(getCollectionPlural()).post(getClazz(), newObject);
        String id;
        if (result instanceof Identifiable) {
            id = ((Identifiable) result).getId();
        } else if (result instanceof ObjectSet) {
            id = ((ObjectSet) result).getId();
        } else {
            throw new SkyeException("Cannot resolve id for " + result.getClass().getName());
        }
        saveAlias(id);
        output.success("Created " + getCollectionSingular() + " with id " + id);
    }

    /**
     * Deletes an instance of this endpoint object
     */
    public void delete() {
        String id = resolveAlias(dynamicParams.get("id"));
        if (id == null)
            throw new CliException("You must provide an id to delete a " + getCollectionSingular());

        else {
            getResource(getCollectionPlural() + "/" + id).delete();

            output.success("Deleted " + getCollectionSingular() + " with id " + id);
        }
    }
}
