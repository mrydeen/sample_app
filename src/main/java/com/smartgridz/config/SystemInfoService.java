package com.smartgridz.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This class provides access to system and product level information.
 */
@Service
public class SystemInfoService {

    private static final Logger LOG = LoggerFactory.getLogger(SystemInfoService.class);

    /**
     * This method will return the Product UUID that was generated when the
     * product was installed.
     *
     * The UUID is created with uuidgen so it should not have a new line
     * at the end.
     */
    public String getNodeUuid() {
        try {
            Path uuidPath = Path.of(SystemDefaults.NODE_UUID);
            String UUID = Files.readString(uuidPath);
            return UUID;
        } catch (IOException ioe) {
            LOG.error("Failed to read node UUID", ioe);
        }
        return "";
    }
}
