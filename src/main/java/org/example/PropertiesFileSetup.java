package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import static org.example.TestData.PROP_FILE_PATH;

public class PropertiesFileSetup {

    public Properties setProperties() {
        Properties properties = new Properties();
        Path propertiesPath = Path.of(PROP_FILE_PATH);

        try (InputStream inputStream = Files.newInputStream(propertiesPath)) {
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to load properties from " + propertiesPath.toAbsolutePath(), e);
        }
    }
}
