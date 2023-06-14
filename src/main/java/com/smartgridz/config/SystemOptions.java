package com.smartgridz.config;

import java.util.HashMap;

/**
 * This file contains all of the system options for this application.
 *
 */
public class SystemOptions {

    private static HashMap<String, Option> OPTION_MAP = new HashMap<String, Option>();

    // ************************************************************************
    // Email settings
    // ************************************************************************
    public static Option EMAIL_HOST = new Option("email.host");
    public static Option EMAIL_PORT = new Option("email.port", "25");
    public static Option EMAIL_USERNAME = new Option("email.username");
    public static Option EMAIL_PASSWORD = new Option("email.password");
    public static Option EMAIL_SMTP_AUTH = new Option("email.smtp.auth", "true");
    public static Option EMAIL_SMTP_STARTTLS_ENABLE = new Option("email.smtp.starttls.enable", "true");
    public static Option FILE_REPOSITORY = new Option("file.repository.dir", "/tmp");

    /**
     * Add all of these options to the OPTION MAP for easy lookup.
     */
    static {
        OPTION_MAP.put(EMAIL_HOST.getOption(), EMAIL_HOST);
        OPTION_MAP.put(EMAIL_PORT.getOption(), EMAIL_PORT);
        OPTION_MAP.put(EMAIL_USERNAME.getOption(), EMAIL_USERNAME);
        OPTION_MAP.put(EMAIL_PASSWORD.getOption(), EMAIL_PASSWORD);
        OPTION_MAP.put(EMAIL_SMTP_AUTH.getOption(), EMAIL_SMTP_AUTH);
        OPTION_MAP.put(EMAIL_SMTP_STARTTLS_ENABLE.getOption(), EMAIL_SMTP_STARTTLS_ENABLE);
        OPTION_MAP.put(FILE_REPOSITORY.getOption(), FILE_REPOSITORY);
    }

    /**
     * Provide the ability to get the default options if they exist.
     */
    public static Option getOption(String option) {
        return OPTION_MAP.get(option);
    }
}
