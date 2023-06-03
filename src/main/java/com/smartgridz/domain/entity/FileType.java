package com.smartgridz.domain.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum FileType {
    /**
     * The Power System Simulator for Engineering (PSS/E) file formats:
     * Raw power flow data.
     */
    PSSE_RAW("PSS/E, only power flow data"),
    /**
     * Only dynamics data.
     */
    PSSE_DYR("PSS/E only dynamics data"),
    /**
     * Only geomagnetically induced current data.
     */
    PSSE_GIC("PSS/E only geomagnetically induced current data"),

    /**
     * Raw power flow data.
     */
    PSLF_EPC("PSLF only power flow data"),

    /**
     * Only dynamics data.
     */
    PSLF_DYD("PSLF only dynamics data");


    private static final Logger LOG = LoggerFactory.getLogger(FileType.class);

    private final String fileType;
    FileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileType() {
        return fileType;
    }

    public static FileType getFromString(String fileType) {
        try {
            return FileType.valueOf(fileType);
        } catch (Exception e) {
            LOG.error("Unknown file type: {}", fileType);
            throw e;
        }
    }
}
