package com.smartgridz;

import com.smartgridz.controller.dto.FileDto;
import com.smartgridz.domain.entity.FileType;
import com.smartgridz.service.FileService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileTest {

    @Autowired
    FileService fileService;

    @AfterEach
    public void tearDown() throws Exception {
        testDeleteAllFiles();
    }

    @Test
    public void testFilePSSE() throws Exception {
        //arrange
        FileDto fileDto = createTestFile();

        fileDto.setDescription("test desc");

        //assert
        Assert.isTrue("test desc".equals(fileDto.getDescription()), "Failed to set description");
        Assert.isTrue(fileDto.getFileFormatVersion().equals("33"), "Failed to set file format version");
        Assert.isTrue(fileDto.getSizeInBytes() > 0, "File size error");
        Assert.isTrue(fileDto.getAddedBy() != null && fileDto.getAddedBy().equals(1L), "Failed to set user id");  // FIXME: this should be the session user.
    }

    @Test
    public void testAddFile() throws Exception {
        //arrange
        FileDto fileDto = createTestFile();

        //act
        fileService.saveFile(fileDto);
        List<FileDto> files = fileService.findAllFiles();

        //assert
        Assert.isTrue(files.size() > 0, " unable to add file record.");
    }

    // This should cause a constraint violation: Cannot add or update a child row: a foreign key constraint fails (`smartgridz`.`files`, CONSTRAINT `files_ibfk_1` FOREIGN KEY (`added_by`) REFERENCES `users` (`id`))
    @Test
    public void testAddFileWithBadUserId() throws Exception {

        boolean failed = false;

        //arrange
        FileDto fileDto = createTestFile();
        fileDto.setAddedBy(890L);

        //act
        try {
            fileService.saveFile(fileDto);
        } catch(Exception e) {
            System.out.println("GRIDZ: There should be an exception");
            failed=true;
        }

        //assert
        Assert.isTrue(failed, " constraint violation test failed");
    }

    @Test
    public void testFindSecondFile() throws Exception {
        //arrange
        FileDto fileDto = createTestFile();

        //act
        fileService.saveFile(fileDto);
        fileService.saveFile(fileDto);
        FileDto file = fileService.findByCanonicalFilename(fileDto.getPathname() + "/" + fileDto.getFilename() + ".1");

        //assert
        Assert.isTrue(file != null, " unable to add file record.");
    }

    @Test
    public void testFindFileById() throws Exception {

        //arrange
        FileDto fileDto = createTestFile();

        //act
        fileService.saveFile(fileDto);
        List<FileDto> files = fileService.findAllFiles();

        //assert
        Assert.isTrue(fileService.findById(files.get(0).getId()).size() > 0, " unable to retrieve record by ID.");
        System.out.println(fileService.findById(files.get(0).getId()));
    }

    @Test
    public void testFindCanonicalFilename() throws Exception {
        //arrange
        FileDto fileDto = createTestFile();

        //act
        fileService.saveFile(fileDto);
        //assert
        fileDto = fileService.findByCanonicalFilename(fileDto.getPathname() + "/" + fileDto.getFilename());

        Assert.isTrue(fileDto.getFilename().length() > 0 , "Failed to find file in DB.");
    }


    @Test
    public void testFindAllFiles() throws Exception {
        //arrange
        FileDto fileDto = createTestFile();

        //act
        fileService.saveFile(fileDto);
        //assert
        Assert.isTrue(fileService.findAllFiles().size() > 0, "Failed to find files in DB.");
    }

    @Test
    public void testDeleteFileById() throws Exception {

        //arrange
        fileService.deleteAllFiles();
        FileDto fileDto = createTestFile();

        //act
        fileService.saveFile(fileDto);
        List<FileDto> files = fileService.findAllFiles();

        System.out.println(files.get(0));

        //assert
        fileService.deleteFileById(files.get(0).getId());
        Assert.isTrue(fileService.findById(files.get(0).getId()).size() == 0, " unable to delete file by ID.");

    }

    @Test
    public void testDeleteAllFiles() throws Exception {
        //arrange
        FileDto fileDto = createTestFile();

        //act
        fileService.deleteAllFiles();

        //assert
        Assert.isTrue(fileService.findAllFiles().size() == 0, "Failed to delete files in DB.");
    }

    private FileDto createTestFile() {
        final String filename = "foobar.psse";
        final String pathname = "/tmp";

        FileDto dto = new FileDto();
        dto.setFilename(filename);
        dto.setPathname(pathname);
        dto.setFileType(FileType.PSSE_RAW);
        dto.setCreateDate(new Date());
        dto.setSizeInBytes(20L);
        dto.setAddedBy(1L);
        dto.setFileFormatVersion("33");

        System.out.println();

        return dto;
    }
}
