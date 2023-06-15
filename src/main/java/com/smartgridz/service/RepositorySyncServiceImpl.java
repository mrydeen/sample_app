package com.smartgridz.service;

import com.smartgridz.config.SystemDefaults;
import com.smartgridz.config.SystemOptionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class RepositorySyncServiceImpl implements RepositorySyncService{
    private static final Logger LOG = LoggerFactory.getLogger(RepositorySyncServiceImpl.class);

    private final FileService fileService;

    private final SystemOptionsService systemOptionsService;

    private final UserService userService;

    public RepositorySyncServiceImpl(FileService fileService, SystemOptionsService systemOptionsService, UserService userService) {
        this.fileService = fileService;
        this.systemOptionsService = systemOptionsService;
        this.userService = userService;

        // Create the repository directory if it doesn't exist.
        createRepositoryDirectory();

        // Sync the repository on startup  TODO: run an auto-sync thread?
        syncRepository();
    }

    @Override
    public void syncRepository() {

        // Run the re-sync to match repository and db refs.
        try {
            Thread thread = new Thread(new RepositorySync(fileService, systemOptionsService, userService));
            thread.start();
            thread.join();
        } catch(Exception e) {
            LOG.error("Problem syncing repository");
            LOG.error(e.getMessage());
        }
    }

    private void createRepositoryDirectory() {
        java.io.File file = new java.io.File(SystemDefaults.REPOSITORY_PATH);
        if(!file.isDirectory()) {
            file.mkdir();
        }
    }
}
