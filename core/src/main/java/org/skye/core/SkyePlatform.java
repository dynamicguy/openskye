package org.skye.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The Skye Platform information
 */
public class SkyePlatform {

    private static final Logger LOGGER = LoggerFactory.getLogger(SkyePlatform.class);

    public SkyePlatform() {
        Properties prop = new Properties();

        try {
            InputStream releaseStream = SkyePlatform.class.getClassLoader().getResourceAsStream("skye-release.properties");
            if (releaseStream == null)
                LOGGER.warn("Unable to find skye-release.properties");
            else
                prop.load(releaseStream);
        } catch (IOException e) {
            LOGGER.warn("Unable to load release information", e);
        }
    }

    public String getRelease() {
        return "Bowman";
    }

    public String getVersion() {
        return "0.0.1";
    }

    public String getStatus() {
        return "active";
    }
}
