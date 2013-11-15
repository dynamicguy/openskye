package org.openskye.cli.commands;

import com.google.common.base.CaseFormat;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.apache.commons.beanutils.BeanUtils;
import org.openskye.cli.ConsoleLogger;
import org.openskye.cli.SkyeCliSettings;
import org.openskye.cli.commands.fields.EnumerationField;
import org.openskye.cli.commands.fields.Field;
import org.openskye.cli.commands.fields.PropertiesField;
import org.openskye.cli.commands.fields.ReferenceField;
import org.openskye.core.SkyeException;
import org.openskye.domain.dao.PaginatedResult;

import javax.ws.rs.core.MediaType;
import java.io.Console;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The interface for an executable command
 */
public abstract class ExecutableCommand {

    protected SkyeCliSettings settings;
    protected Client client = Client.create();
    protected ConsoleLogger output;

    public List<Field> getFields() {
        // By default, a command has no fields
        return new ArrayList<Field>();
    }

    public void initialize(SkyeCliSettings settings, ConsoleLogger consoleLogger) {
        this.settings = settings;
        this.output = consoleLogger;
    }

    protected WebResource.Builder getResource(String path) {
        WebResource webResource = client
                .resource(settings.getUrl() + path);
        WebResource.Builder result = webResource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE);

        // If we have an API key in place then we will use it

        if (settings.getApiKey() != null) {
            result.header("X-Api-Key", settings.getApiKey());
        }
        return result;
    }

    public abstract void execute();

    public abstract String getCommandName();

    public Console getConsole() {
        return System.console();
    }

    protected void selectEnum(EnumerationField field, Object newObject) {
        List<?> choices = field.getAllEnumOptions();

        int i = 1;
        output.message("Please select a " + field.getName() + " from the choices below");
        for (Object option : choices) {
            output.raw(" " + i + "/ " + option.toString());
            i++;
        }
        while (true) {
            String option = getConsole().readLine("Enter choice:");
            int position = Integer.parseInt(option);
            try {
                BeanUtils.setProperty(newObject, field.getName(), field.getEnum(position - 1));
                break;
            } catch (Exception e) {
                throw new SkyeException("Unable to assign enum value to this object", e);
            }

        }
    }

    protected void selectReferenceField(ReferenceField field, Object newObject) {

        // We need to display a list of the available options for the reference field
        // and then let the user choose one

        PaginatedResult paginatedResult = getResource(field.getResource()).get(PaginatedResult.class);
        if (paginatedResult.getResults().size() == 0) {
            output.message("You must have at least 1 " + field.getName() + " to create this object");
            throw new SkyeException("Objects missing that need to be created");

        } else {
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

    protected void setPropertiesField(PropertiesField props, Object newObject) {
        output.message("Please enter the properties and values.");
        while (true) {
            String property = getConsole().readLine("Property: ");
            String value = getConsole().readLine("Value: ");
            props.addProperty(property, value);
            String answer = getConsole().readLine("Do you have any more properties? Y/N ");
            if (answer.equalsIgnoreCase("N")) {
                break;
            }
        }
        try {
            BeanUtils.setProperty(newObject, props.getName(), props.getProperties());
        } catch (Exception e) {
            throw new SkyeException("Unable to add properties to object", e);
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

}
