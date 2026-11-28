package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.example.TestData.PROP_RESOURCE_NAME;

public class PropertiesFileSetup {

    /**
     * Loads configuration from the classpath, so it works regardless of the
     * working directory and when the code is packaged as a jar.
     */
    public Properties setProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = PropertiesFileSetup.class.getClassLoader()
                .getResourceAsStream(PROP_RESOURCE_NAME)) {
            if (inputStream == null) {
                throw new IllegalStateException(
                        "Classpath resource not found: " + PROP_RESOURCE_NAME);
            }
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to load properties from classpath resource " + PROP_RESOURCE_NAME, e);
        }
    }
}
