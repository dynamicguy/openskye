package org.openskye.cli.commands;

import com.beust.jcommander.DynamicParameter;
import com.beust.jcommander.Parameter;
import com.google.common.base.CaseFormat;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import org.apache.commons.beanutils.BeanUtils;
import org.openskye.cli.CliException;
import org.openskye.cli.ConsoleLogger;
import org.openskye.cli.SkyeCliSettings;
import org.openskye.cli.commands.fields.*;
import org.openskye.core.SkyeException;
import org.openskye.domain.Node;
import org.openskye.domain.NodeArchiveStoreInstance;
import org.openskye.domain.NodeRole;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.Console;
import java.util.*;

/**
 * The interface for an executable command in the Skye command line interface.
 */
public abstract class ExecutableCommand {

    /**
     * A set of dynamic parameters passed from the command line and used for commands that require additional parameters
     * beyond the command name (such as create and update)
     */
    @DynamicParameter(names = {"-P"}, description = "Additional parameters for this command")
    public Map<String, String> dynamicParams = new HashMap<>();
    /**
     * An alias for a created object (called optionally from the command line with the "-A" flag)
     */
    @Parameter(names = "-A", description = "A signal to save a created/listed id as a given alias")
    protected String alias;
    /**
     * User settings
     *
     * @see SkyeCliSettings
     */
    protected SkyeCliSettings settings;
    /**
     * A WebResource client to handle REST requests sent to the API from the command line
     *
     * @see com.sun.jersey.api.client.Client
     */
    protected Client client = Client.create();
    /**
     * The Skye CLI ConsoleLogger for output messages
     *
     * @see ConsoleLogger
     */
    protected ConsoleLogger output;

    /**
     * Returns the fields required for a particular command object
     *
     * @return a list of {@link Field}s
     */
    public List<Field> getFields() {
        // By default, a command has no fields
        return new ArrayList<>();
    }

    /**
     * Initializes the Skye command line by setting the logger and settings
     *
     * @param settings      the user settings
     * @param consoleLogger the console logger
     */
    public void initialize(SkyeCliSettings settings, ConsoleLogger consoleLogger) {
        this.settings = settings;
        this.output = consoleLogger;
    }

    /**
     * Sends a REST request to the Skye API. The given path is converted to a REST request using the user settings to
     * create the full URL, and then sent to the API. The result is the result of the rest request, which can be a newly
     * created object or a paginated result, depending on the request.
     *
     * @param path the appending path to a specific Skye API resource, used to find the required endpoint.
     *
     * @return the result of the REST request
     */
    protected WebResource.Builder getResource(String path) {
        return getResource(path, null);
    }

    /**
     * Sends a REST request to the Skye API. The given path is converted to a REST request using the user settings to
     * create the full URL, and then sent to the API. The result is the result of the rest request, which can be a newly
     * created object or a paginated result, depending on the request.
     *
     * @param path the appending path to a specific Skye API resource, used to find the required endpoint.
     * @param params map of params that should be set as query params for the http request.
     *
     * @return the result of the REST request
     */
    protected WebResource.Builder getResource(String path, MultivaluedMap params) {
        WebResource webResource = client.resource(settings.getUrl() + path);
        if (params != null && params.size() > 0) {
            webResource = webResource.queryParams(params);
        }
        WebResource.Builder result = webResource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_TYPE);

        // If we have an API key in place then we will use it

        if (settings.getApiKey() != null) {
            result.header("X-Api-Key", settings.getApiKey());
        }
        return result;
    }

    /**
     * Parses and executes a command sent from the command line.
     */
    public abstract void execute();

    /**
     * Gets the name of the command.
     *
     * @return the command name
     */
    public abstract String getCommandName();

    /**
     * Accesses the system's console.
     *
     * @return the user's console
     *
     * @see Console
     */
    public Console getConsole() {
        return System.console();
    }

    /**
     * Assigns a number to a <code>NumberField</code> for the given object. Used if one of the creation parameters is a
     * numeric type
     *
     * @param field     the <code>NumberField</code> that requires a value
     * @param newObject the object to be modified or created
     *
     * @return the modified object
     *
     * @see NumberField
     */
    protected Object enterNumber(NumberField field, Object newObject) {
        while (true) {
            try {
                String input = getConsole().readLine("Enter " + field.getName() + ":");
                Long value = Long.parseLong(input);
                BeanUtils.setProperty(newObject, field.getName(), value);
                break;
            } catch (Exception e) {
                throw new SkyeException("Unable to assign number value to this object", e);
            }
        }
        return newObject;
    }

    /**
     * Assigns a text value to a <code>TextField</code>. Used to assign raw text to a text based field
     *
     * @param field     the <code>TextField </code> that requires a value
     * @param newObject the object to be modified or created
     *
     * @return the modified object
     *
     * @see TextField
     */
    protected Object enterText(TextField field, Object newObject) {
        while (true) {
            try {
                String input = getConsole().readLine("Enter " + field.getName() + ":");
                BeanUtils.setProperty(newObject, field.getName(), input);
                break;
            } catch (Exception e) {
                throw new SkyeException("Unable to assign text value to this object", e);
            }
        }
        return newObject;
    }

    /**
     * Assigns a true/false value to a <code>BooleanField</code>.
     *
     * @param field     the <code>BooleanField</code> that requires a value
     * @param newObject the object to be modified or created
     *
     * @return the modified object
     *
     * @see BooleanField
     */
    protected Object enterBoolean(BooleanField field, Object newObject) {
        while (true) {
            try {
                String input = getConsole().readLine("Enter " + field.getName() + " (true/false):");
                Boolean value = Boolean.parseBoolean(input);
                BeanUtils.setProperty(newObject, field.getName(), value);
                break;
            } catch (Exception e) {
                throw new SkyeException("Unable to assign boolean value to this object", e);
            }
        }
        return newObject;
    }

    /**
     * Selects a value for an <code>EnumerationField</code>. All possible values of the enumeration field are retrieved,
     * and the user selects the appropriate value.
     *
     * @param field     the <code>EnumerationField</code>, a field for Enumeration type parameters
     * @param newObject the object to be modified
     *
     * @return the modified object
     *
     * @see EnumerationField
     */
    protected Object selectEnum(EnumerationField field, Object newObject) {
        List<? extends Enum> choices = field.getAllEnumOptions();
        try {
            String choice = dynamicParams.get(field.getName());
            Enum enumChoice = Enum.valueOf(field.getClazz(), choice);

            if (choices.contains(enumChoice)) {

                BeanUtils.setProperty(newObject, field.getName(), enumChoice);

            } else {
                output.error("Value not allowed for field: " + field.getName());
                output.message("The following values are allowed for this field: ");
                for (Object o : choices) {
                    output.raw(o.toString());
                    throw new SkyeException("Unable to set Enum field");
                }
            }
        } catch (Exception e) {
            throw new SkyeException("Unable to set Enum field", e);
        }
        return newObject;
    }

    protected String resolveAlias(String s) {
        String aliasMatch = settings.getIdMap().get(s);
        if (aliasMatch != null) {
            output.message("Alias " + s + " resolved to " + aliasMatch);
            return aliasMatch;
        } else {
            return s;
        }
    }

    /**
     * Assigns a value to a <code>ReferenceField</code>. The user enters either an id or a known alias as part of the
     * create command. The supplied id is then used to search for the required object, which is then assigned to the
     * object to be created/modified.
     *
     * @param field     the <code>ReferenceField</code> for the object
     * @param newObject the object being created/modified
     *
     * @return the newly modified object
     *
     * @see ReferenceField
     */
    protected Object selectReferenceField(ReferenceField field, Object newObject) {
        // If the user supplied id is a known alias, replace it with the actual id
        String id = resolveAlias(dynamicParams.get(field.getName()));

        try {
            Object result = getResource(field.getResource() + "/" + id).get(field.getClazz());
            output.raw(result.toString());
            BeanUtils.setProperty(newObject, field.getName(), result);
        } catch (Exception e) {
            throw new SkyeException("Unable to build reference field for " + field, e);
        }

        return newObject;
    }

    /**
     * Assigns a set of properties to a <code>PropertiesField</code>. The user can enter a set of configuration
     * properties when creating an object. They are parsed and assigned here.
     *
     * @param props     the <code>PropertiesField</code>
     * @param newObject the object being created or modified
     *
     * @return the newly modified object
     *
     * @see PropertiesField
     */
    protected Object setPropertiesField(PropertiesField props, Object newObject) {
        if (dynamicParams.get(props.getName()) != null) {
            String properties = dynamicParams.get(props.getName());
            String[] propertyPairs = properties.split(",");
            for (String pair : propertyPairs) {
                String[] pairSplit = pair.split(":");
                props.addProperty(pairSplit[0], pairSplit[1]);
            }
            try {
                BeanUtils.setProperty(newObject, props.getName(), props.getProperties());
            } catch (Exception e) {
                throw new SkyeException("Unable to add properties to object", e);
            }
        }
        return newObject;

    }

    /**
     * Assigns a set of properties to a <code>NodeRolesField</code>. The user can enter a set of node
     * roles when creating an object. They are parsed and assigned here.
     *
     * @param field     the <code>NodeRolesField</code>
     * @param newObject the object being created or modified
     *
     * @return the newly modified object
     *
     * @see NodeRolesField
     */
    protected Object setNodeRolesField(NodeRolesField field, Object newObject) {
        final String PRIMARY = "PRIMARY";
        final String REPLICA = "REPLICA";

        if (dynamicParams.get(field.getName()) != null) {
            List<NodeArchiveStoreInstance> nodes = new ArrayList<>();
            String value = dynamicParams.get(field.getName());
            String[] pairs = value.split(",");
            for (String pair : pairs) {
                String[] tokens = pair.split(":");
                NodeArchiveStoreInstance link = new NodeArchiveStoreInstance();
                link.setNode(getResource("nodes/" + resolveAlias(tokens[0])).get(Node.class));
                if ( PRIMARY.startsWith(tokens[1].toUpperCase()) ) {
                    link.setNodeRole(NodeRole.PRIMARY);
                } else if ( REPLICA.startsWith(tokens[1].toUpperCase()) ) {
                    link.setNodeRole(NodeRole.REPLICA);
                } else {
                    throw new CliException("Invalid node role: "+tokens[1]);
                }
                nodes.add(link);
            }
            try {
                BeanUtils.setProperty(newObject, field.getName(), nodes);
            } catch (Exception e) {
                throw new SkyeException("Unable to add node roles to object", e);
            }
        }
        return newObject;

    }

    /**
     * Retrieves the list of fields for this command in String form.
     *
     * @return a string list of field names for this command.
     */
    public Collection<? extends String> getFieldNames() {
        List<String> fieldNames = new ArrayList();
        for (Field field : getFields()) {
            fieldNames.add(field.getName());
        }
        return fieldNames;
    }

    /**
     * Converts a string to lower camel case. Used to create resource strings.
     *
     * @param name the string to convert
     *
     * @return the converted String
     */
    protected String toCamel(String name) {
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name);
    }

    /**
     * If an alias was given with the -A option, store the created Id with that alias in settings.
     *
     * @param id the created id
     */
    protected void saveAlias(String id) {
        // If an alias was given with the -A option, store the created Id with that alias in settings
        if (alias != null) {
            output.message("Id " + id + " saved to alias " + alias);
            settings.getIdMap().put(alias, id);
            settings.save();
        }
    }

}
