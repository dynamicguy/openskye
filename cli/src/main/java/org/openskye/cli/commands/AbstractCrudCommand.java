package org.openskye.cli.commands;

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
import org.openskye.domain.Task;
import org.openskye.domain.dao.PaginatedResult;

import java.io.Console;
import java.lang.reflect.InvocationTargetException;
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
    private List<String> id;

    public abstract List<Field> getFields();

    public abstract Class getClazz();

    @Override
    public void execute() {
        // Ensure we are logged in
        settings.mustHaveApiKey();

        if (list) {
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
                } else if(field instanceof PropertiesField){
                    setPropertiesField((PropertiesField)field, newObject);
                }  else if(field instanceof EnumerationField){
                    selectEnum((EnumerationField)field, newObject);
                }
            }
            Identifiable result = (Identifiable) getResource(getCollectionPlural()).post(getClazz(), newObject);

            output.success("Created " + getCollectionSingular() + " with id " + result.getId());
        } else if (delete) {
            if (id == null)
                throw new CliException("You must provide an id to delete a " + getCollectionSingular());

            for(String idInstance : id) {
                getResource(getCollectionPlural() + "/" + idInstance).delete();

                output.success("Deleted " + getCollectionSingular() + " with id " + idInstance);
            }
        }
    }

    private void selectEnum(EnumerationField field, Object newObject) {
        List<?> choices = field.getAllEnumOptions();

        int i=1;
        output.message("Please select a " + field.getName() + "from the choices below");
        for(Object option : choices){
            output.raw(" " + i + "/ " + option.toString());
            i++;
        }
        while (true) {
            String option = getConsole().readLine("Enter choice:");
            int position = Integer.parseInt(option);
            try {
                BeanUtils.setProperty(newObject, field.getName(), field.getEnum(position));
                break;
            } catch (Exception e) {
                throw new SkyeException("Unable to assign enum value to this object", e);
            }

        }
    }

    private void selectReferenceField(ReferenceField field, Object newObject) {

        // We need to display a list of the available options for the reference field
        // and then let the user choose one

        PaginatedResult paginatedResult = getResource(field.getResource()).get(PaginatedResult.class);
        if(paginatedResult.getResults().size()==0){
            if(field.getClazz()==Task.class){
                try {
                    Object result = Class.forName(field.getClazz().getCanonicalName()).newInstance();
                    BeanUtils.setProperty(newObject, field.getName(), result);
                } catch (Exception e) {
                    throw new SkyeException("Can't create blank object", e);
                }

            }
            else{
                output.message("You must have at least 1 " + field.getName() + " to create this object");
                throw new SkyeException("Objects missing that need to be created");
            }
        }
        else{
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

    }

    public void setPropertiesField(PropertiesField props, Object newObject){
        output.message("Please enter the properties and values.");
        while(true){
            String property = getConsole().readLine("Property: ");
            String value = getConsole().readLine("Value: ");
            props.addProperty(property,value);
            String answer = getConsole().readLine("Do you have any more properties? Y/N ");
            if(answer.equalsIgnoreCase("N")){
                break;
            }
        }
        try {
            BeanUtils.setProperty(newObject, props.getName(), props.getProperties());
        } catch (Exception e) {
            throw new SkyeException("Unable to add properties to object",e);
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
}
