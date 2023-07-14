package com.smartgridz.service;

import com.smartgridz.config.SystemOptions;
import com.smartgridz.config.SystemOptionsService;
import com.smartgridz.controller.dto.FileDto;
import com.smartgridz.domain.entity.File;
import com.smartgridz.domain.entity.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Configuration
public class RepositorySync implements Runnable{
    private static final Logger LOG = LoggerFactory.getLogger(RepositorySync.class);

    private final FileService fileService;
    private final SystemOptionsService systemOptionsService;

    private final UserService userService;

    public RepositorySync(FileService fileService, SystemOptionsService systemOptionsService, UserService userService) {
        this.fileService = fileService;
        this.systemOptionsService = systemOptionsService;
        this.userService = userService;
    }
    @Override
    public void run() {
        syncRepositoryToDatabase();
        syncDatabaseToRepository();
    }

    /**
     * Description: Mark database records invalid if the corresponding file is missing.
     */
    private void syncDatabaseToRepository() {
        List<FileDto> files = fileService.findAllFilesAsDto();

        for(FileDto file : files) {
            java.io.File tmp = new java.io.File(file.getPathname() + "/" + file.getFilename());
            if(!tmp.exists()) {
                // A database record exists that doesn't have a corresponding file on the file system.
                file.setValid(false);
                fileService.update(file); // TODO: add invalid reason
            }
        }
    }

    /**
     * Description: Create missing database records from existing files in the repository (best effort).
     */
    private void syncRepositoryToDatabase() {

        String repository = systemOptionsService.getOption(SystemOptions.FILE_REPOSITORY);
        if (repository == null) {
            LOG.error("File repository directory is not defined, skipping repository clean up.");
            return;
        }

        // Open the repository directory and search for files.
        scanDirectory(new java.io.File(repository));
    }

    private void scanDirectory(java.io.File directory) {

        if(directory == null) return;

        java.io.File[] files = directory.listFiles();
        if(files == null) return;

        for (java.io.File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file);
            } else {
                processFile(file);
            }
        }
    }

    private void processFile(java.io.File file) {
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(file.getCanonicalPath());

            FileDto f = fileService.findByCanonicalFilename(file.getCanonicalPath());
            if(f == null) {
                FileDto fileDto = new FileDto();
                fileDto.setFilename(file.getName());
                fileDto.setPathname(path.getParent().toString());
                fileDto.setFileType(FileType.PSSE_RAW);  // TODO: This needs to be determined by scanning the file metadata. (fill in later if we can).
                fileDto.setCreateDate(new Date());
                fileDto.setSizeInBytes(file.length());
                fileDto.setDescription("Recreated by RepositorySync service");
                fileDto.setValid(true);

                Long userId = userService.getUserId();

                fileDto.setAddedBy(userId != null ? userId : 1L);  // The test will break if the admin account isn't there.
                fileDto.setFileFormatVersion("33");      // TODO: This needs to be determined by scanning the file metadata. (only versions after 33 have a version, maybe this is editable).

                fileService.saveFile(fileDto);

                LOG.info("Recreated database entry from file for: " + fileDto.getPathname() + "/" + fileDto.getFilename());
            }

        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
