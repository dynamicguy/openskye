package org.skye.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.commons.lang.SystemUtils;
import org.skye.core.SkyeException;

import java.io.File;
import java.io.IOException;

/**
 * A very simple handling storing settings for the Skye CLI
 */
@Data
public class SkyeCliSettings {

    private String apiKey = "123";
    private String url = "http://localhost:5000/api/1/";

    public static SkyeCliSettings load() {
        // Load the settings from the user's home directory
        if (getSettingsFile().exists()) {
            ObjectMapper om = new ObjectMapper();
            try {
                return om.readValue(getSettingsFile(), SkyeCliSettings.class);
            } catch (IOException e) {
                throw new SkyeException("Unable to parse ~/.skye/settings.json", e);
            }
        } else
            return new SkyeCliSettings();
    }

    private static File getSettingsFile() {
        try {
            File settingFile = new File(SystemUtils.getUserHome().getCanonicalPath() + "/.skye/settings.json");
            if (!settingFile.exists()) {
                settingFile.mkdirs();
                settingFile.delete();
            }
            return settingFile;
        } catch (IOException e) {
            throw new SkyeException("Unable to access ~/.skye/settings.json", e);
        }
    }

    public void save() {
        // Save the settings back to the user's home directory
        ObjectMapper om = new ObjectMapper();
        try {
            om.writeValue(getSettingsFile(), this);
        } catch (IOException e) {
            throw new SkyeException("Unable to parse ~/.skye/settings.json", e);
        }
    }
}
