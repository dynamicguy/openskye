package org.openskye.cli.commands;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.google.common.base.CaseFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.openskye.cli.CliException;
import org.openskye.cli.commands.fields.*;
import org.openskye.cli.util.ObjectTableView;
import org.openskye.core.SkyeException;
import org.openskye.domain.Identifiable;
import org.openskye.domain.dao.PaginatedResult;

import java.io.Console;
import java.util.*;

/**
 * An Abstract Base for Crud-like commands which are in
 * place for setting up the metadata for Skye processing
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = false)
public abstract class AbstractCrudCommand extends ExecutableCommand {



    @Parameter(names = "--list")
    protected boolean list;
    @Parameter(names = "--delete")
    protected boolean delete;
    @Parameter(names = "--create")
    protected boolean create;
    @Parameter(names = "--get")
    protected boolean get;
    @Parameter
    private List<String> id;



    public abstract List<Field> getFields();

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
        } else if(get){
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

    public String getCollectionSingular() {
        return toCamel(getClazz().getSimpleName());
    }

    public String getCollectionPlural() {
        return toCamel(getClazz().getSimpleName() + "s");
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

    private void get() {
        String id = dynamicParams.get("id");
        if(id==null){
            output.error("You must enter an id");
        }
        else{
            Object result = getResource(getCollectionPlural()+"/"+id).get(getClazz());
            //TODO: Print this result in a nicer way
            output.raw(result.toString());
        }
    }

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
                newObject=selectReferenceField((ReferenceField) field, newObject);
            } else if (field instanceof PropertiesField) {
                newObject=setPropertiesField((PropertiesField) field, newObject);
            } else if (field instanceof EnumerationField) {
                selectEnum((EnumerationField) field, newObject);
            }
        }
        Identifiable result = (Identifiable) getResource(getCollectionPlural()).post(getClazz(), newObject);

        output.success("Created " + getCollectionSingular() + " with id " + result.getId());
    }

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
