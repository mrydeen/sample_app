package com.smartgridz.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents the different role types in the system.  This will be used for
 * RBAC.
 */

public enum RoleType {
    /**
     * The administrator user has all powers to modify configurations via UI or APIs
     */
    ADMINISTRATOR("ROLE_ADMIN"),
    /**
     * The operator user is a read only user.  They can view the UI and perform GETs
     * at the API level
     */
    OPERATOR("ROLE_USER");

    private static final Logger LOG = LoggerFactory.getLogger(RoleType.class);

    private final String springAuthType;
    RoleType(String springAuthType) {
        this.springAuthType = springAuthType;
    }

    public String getSpringAuthType() {
        return springAuthType;
    }

    public static RoleType getFromString(String roleType) {
        try {
            return RoleType.valueOf(roleType);
        } catch (Exception e) {
            LOG.error("Unknown Role type: {}", roleType);
            throw e;
        }
    }
}
