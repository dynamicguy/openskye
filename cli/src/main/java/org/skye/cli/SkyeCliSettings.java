package org.skye.cli;

import lombok.Data;

/**
 * A very simple handling storing settings for the Skye CLI
 */
@Data
public class SkyeCliSettings {

    private String apiKey = "123";
    private String url = "http://localhost:5000/api/1/";

    public void load() {
        // Load the settings from the user's home directory
    }
}
