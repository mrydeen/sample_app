package com.smartgridz.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Properties;

/**
 * This class is used to get access to any of the System options/configuration settings.
 * You can GET/SET/UPDATE the option values.  You can also register to receive notifications
 * for when options are modified.
 */
@Service
public class SystemOptionsService {

    private static final Logger LOG = LoggerFactory.getLogger(SystemOptionsService.class);

    /**
     * This contains all of the properties for the system.  It will be kept up to date
     * as the application changes.
     */
    private Properties SYSTEM_OPTIONS = new Properties();

    /**
     * This method will load up the current properties file.  The location of this file
     * is defined in the SystemDefaults file.
     */
    @PostConstruct
    private void loadPropertyFile() {
        try (InputStream input = new FileInputStream(SystemDefaults.CONFIG_FILE)) {

            // load a properties file
            SYSTEM_OPTIONS.load(input);

        } catch (IOException ex) {
            LOG.error("Failed to load config.properties file.", ex);
        }
    }

    /**
     * This method will look to see if we have an option/property already stored
     * in the file.  Will return if already in the file, otherwise check to see
     * if there is a SystemOptions exists and has a default value.
     *
     * @param option to look for.
     * @return either the value or null if not found and does not have a default.
     */
    public String getOption(String option) {
       // Look to see if we have an option for this already in the file.
       String value = SYSTEM_OPTIONS.getProperty(option);
       if ( value == null) {
           // See if the option has been specified in the SystemOptions file and has
           // a default value.
           Option systemOption = SystemOptions.getOption(option);
           if (systemOption != null) {
               if (systemOption.hasDefault()) {
                   return systemOption.getDefaultValue();
               }
           }
       }
       return value;
    }

    /**
     * Given the current option, get the file value if available, else return
     * the default value of the option.
     *
      * @param option to look for
     * @return the value or null.
     */
    public String getOption(Option option) {
        return getOption(option.getOption());
    }

    /**
     * Set an option using the Option defined in the SystemOptions file.
     *
     * @param option - object to use
     * @param value - to set.
     */
    public void setOption(Option option, String value) {
        setOption(option.getOption(), value);
    }

    /**
     * Set an option.  This will update the properties file and the properities member.
     *
     * @param option - the key to set
     * @param value - the value to set for the key
     */
    synchronized public void setOption(String option, String value) {
        // Update the value.
        SYSTEM_OPTIONS.put(option, value);

        // Now update the file.
        try (OutputStream output = new FileOutputStream(SystemDefaults.CONFIG_FILE)) {

            // save properties to project root folder
            SYSTEM_OPTIONS.store(output, null);

        } catch (IOException io) {
            LOG.error("Failed to save system options:", io);
        }
    }
}
