package org.openskye.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.commons.lang.SystemUtils;
import org.openskye.core.SkyeException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A very simple handling storing settings for the Skye CLI, namely apiKey and URL
 */
@Data
public class SkyeCliSettings {

    /**
     * The principal user's API key
     */
    private String apiKey = null;
    /**
     * The Skye API URL
     */
    private String url = "http://localhost:5000/api/1/";
    /**
     * A current list of aliases created by the user
     */
    private Map<String, String> idMap = new HashMap<>();

    /**
     * Loads the command line interface settings. The settings can be found in a file titled "settings.json" or the
     * default settings
     *
     * @return a SkyeCliSettings instance that is based on either the user's settings file or the default settings if
     *         there is no settings file.
     */
    public static SkyeCliSettings load() {
        // Load the settings from the user's home directory
        if (getSettingsFile().exists()) {
            ObjectMapper om = new ObjectMapper();
            try {
                return om.readValue(getSettingsFile(), SkyeCliSettings.class);
            } catch (IOException e) {
                throw new SkyeException("Unable to parse ~/.openskye/settings.json", e);
            }
        } else
            return new SkyeCliSettings();
    }

    /**
     * Finds the user's settings file. Returns an empty file if there is none.
     *
     * @return the user's settings file, or a blank file if there is none
     */
    private static File getSettingsFile() {
        try {
            File settingFile = new File(SystemUtils.getUserHome().getCanonicalPath() + "/.openskye/settings.json");
            if (!settingFile.exists()) {
                settingFile.mkdirs();
                settingFile.delete();
            }
            return settingFile;
        } catch (IOException e) {
            throw new SkyeException("Unable to access ~/.openskye/settings.json", e);
        }
    }

    /**
     * Save the user's settings back to their home directory.
     */
    public void save() {
        // Save the settings back to the user's home directory
        ObjectMapper om = new ObjectMapper();
        try {
            om.writeValue(getSettingsFile(), this);
        } catch (IOException e) {
            throw new SkyeException("Unable to parse ~/.openskye/settings.json", e);
        }
    }

    /**
     * Checks the apiKey, throws an exception if it is null. This is a check to make sure whoever is running commands
     * has logged in first.
     */
    public void mustHaveApiKey() {
        if (apiKey == null)
            throw new SkyeException("API key not available, please login first");
    }
}
