package org.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.example.TestData.*;

public class PropertiesFileSetup {

    /**
     * Set properties file using FileInputStream
     * @return Properties object
     */
    public Properties setProperties() {

        final Logger LOGGER = Logger.getLogger(PropertiesFileSetup.class.getName());
        Properties prop = new Properties();
        FileInputStream fileInputStream;

        try {
            fileInputStream = new FileInputStream(PROP_FILE_PATH);
            prop.load(fileInputStream);
        }

        catch (FileNotFoundException fife) {
            LOGGER.log(Level.SEVERE, "Properties file not found on the given location", fife);
        }

        catch (IOException e) {
            throw new RuntimeException(e);
        }

        return prop;
    }
}
