package com.smartgridz.config;

/**
 * This class contains some system defaults.
 */
public class SystemDefaults {
    // Base directory of the config.  Would need to change based on OS support
    public static final String INSTALL_PATH = "/opt/smartgridz";
    // The base config directory
    public static final String CONFIG_PATH = INSTALL_PATH + "/config";
    // The system properties file
    public static final String CONFIG_FILE = CONFIG_PATH + "/config.properties";

    // The node install UUID file
    public static final String NODE_UUID = CONFIG_PATH + "/uuid";

    // The product license file if one exists.
    public static final String PRODUCT_LICENSE = CONFIG_PATH + "license.sgz";

    // The public key file if it exists.
    public static final String PUBLIC_KEY = CONFIG_PATH + "smartgridz.key";

    public static final String REPOSITORY_PATH = INSTALL_PATH + "/repository";
}
