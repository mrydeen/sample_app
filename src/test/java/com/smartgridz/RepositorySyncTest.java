package com.smartgridz;

import com.smartgridz.config.SystemOptions;
import com.smartgridz.config.SystemOptionsService;
import com.smartgridz.domain.entity.File;
import com.smartgridz.service.FileService;
import com.smartgridz.service.RepositorySync;
import com.smartgridz.service.RepositorySyncService;
import com.smartgridz.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

import java.nio.file.FileSystems;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RepositorySyncTest {

    private static final Logger LOG = LoggerFactory.getLogger(RepositorySync.class);
    private static String origRepo = "";
    private static final String BASE_DIR = "repository";
    private static final String BASE_TXT = "doc.txt";
    private static final String DIR1 = BASE_DIR + "/dir1";
    private static final String DIR2 = BASE_DIR + "/dir2";
    private static final String DIR1_TXT = DIR1 + "/" + BASE_TXT;
    private static final String DIR2_TXT = DIR2 + "/" + BASE_TXT;

    @Autowired
    FileService fileService;

    @Autowired
    RepositorySyncService repositorySyncService;

    @Autowired
    SystemOptionsService systemOptionsService;

    @Autowired
    UserService userService;

    @AfterEach
    public void tearDown() throws Exception {
        java.io.File f = new java.io.File(DIR2_TXT); f.delete();
        f = new java.io.File(DIR1_TXT); f.delete();
        f = new java.io.File(DIR2); f.delete();
        f = new java.io.File(DIR1); f.delete();
        f = new java.io.File(BASE_DIR); f.delete();

        systemOptionsService.setOption(SystemOptions.FILE_REPOSITORY, origRepo);

        String repo = FileSystems.getDefault().getPath("").toAbsolutePath().toString() + "/";
        fileService.deleteByPathnameAndFilename(repo + DIR1, BASE_TXT);
        fileService.deleteByPathnameAndFilename(repo + DIR2, BASE_TXT);
    }

    @Test
    public void testDirectoryScan() throws Exception {
        //arrange
        String cwd = FileSystems.getDefault().getPath("").toAbsolutePath().toString();
        // Set the repository to a temporary directory, create some subdirectories, and add some files.
        origRepo = systemOptionsService.getOption(SystemOptions.FILE_REPOSITORY);
        systemOptionsService.setOption(SystemOptions.FILE_REPOSITORY, BASE_DIR);
        // Create some directories
        java.io.File dir =  new java.io.File(BASE_DIR);
        dir.mkdir();
        dir = new java.io.File(DIR1);
        dir.mkdir();
        dir = new java.io.File(DIR2);
        dir.mkdir();
        // Create some files
        dir = new java.io.File(DIR1_TXT);
        dir.createNewFile();
        dir = new java.io.File(DIR2_TXT);
        dir.createNewFile();

        //act
        repositorySyncService.syncRepository();

        //assert
        Assert.isTrue(fileService.findByPathnameAndFilename(cwd + "/" + DIR1, BASE_TXT) != null, DIR1_TXT + " is not found in the database.");
        Assert.isTrue(fileService.findByPathnameAndFilename(cwd + "/" + DIR2, BASE_TXT) != null, DIR2_TXT + " is not found in the database.");

        // Phase 2, check that a record becomes invalid if a file is deleted.
        //arrange
        if(dir.exists()) {
            dir.delete();
        }

        //act
        repositorySyncService.syncRepository();

        //assert
        File x = fileService.findByPathnameAndFilename(cwd + "/" + DIR2, BASE_TXT);

        Assert.isTrue(!x.isValid(), DIR2_TXT + " file was not invalidated.");
    }
}
