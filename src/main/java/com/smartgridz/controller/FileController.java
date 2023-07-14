
package com.smartgridz.controller;

import com.smartgridz.config.SystemOptions;
import com.smartgridz.config.SystemOptionsService;
import com.smartgridz.controller.dto.FileDto;
import com.smartgridz.dao.FileDao;
import com.smartgridz.domain.entity.File;
import com.smartgridz.domain.entity.FilePSSE;
import com.smartgridz.domain.entity.FileType;
import com.smartgridz.domain.entity.User;
import com.smartgridz.service.FileService;

import com.smartgridz.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/files")
public class FileController {

    private static final Logger LOG = LoggerFactory.getLogger(FileController.class);

    private final FileService fileService;
    private final SystemOptionsService systemOptionsService;

    private final UserService userService;

    public FileController(FileService fileService, SystemOptionsService systemOptionsService, UserService userService) {
        this.fileService = fileService;
        this.systemOptionsService = systemOptionsService;
        this.userService = userService;
    }

    // handler method to handle list of files
    @GetMapping()
    public String files(Model model){
        List<FileDto> files = fileService.findAllFilesAsDto();
        model.addAttribute("files", files);
        return "file/files";
    }

    // Handler method to handle file add form submit request
    @PostMapping("/add")

    public String saveFile(@RequestParam("file") MultipartFile[] files, @RequestParam("description") String description, @RequestParam("casename") String casename, Model model) {
        String repository = systemOptionsService.getOption(SystemOptions.FILE_REPOSITORY);
        if(repository == null) {
            LOG.error("File repository directory is not defined.");
            return "redirect:/files_add?error";
        }

        // If there is a case name, create a directory for the file(s).
        if(casename != null) {
            java.io.File f = new java.io.File(repository + "/" + casename);
            if (!f.isDirectory()) {
                f.mkdir();
            }
        }

        // Loop through the set of files and add/upload.
        for(MultipartFile file: files) {
            if (file.isEmpty()) {
                return "redirect:/files_add?error";
            }

            FileDto fileDto = new FileDto();
            fileDto.setFilename(file.getOriginalFilename());
            if(casename != null) {
                fileDto.setPathname(repository + "/" + casename);
            } else {
                fileDto.setPathname(repository);
            }
            fileDto.setFileType(FileType.PSSE_RAW);  // TODO: This needs to be determined by scanning the file metadata. (fill in later if we can).
            fileDto.setCreateDate(new Date());
            fileDto.setSizeInBytes(file.getSize());
            fileDto.setDescription(description);
            fileDto.setCasename(casename);
            fileDto.setValid(true);

            Long userId = userService.getUserId();
            fileDto.setAddedBy(userId != null ? userId : 0L);
            fileDto.setFileFormatVersion("33");      // TODO: This needs to be determined by scanning the file metadata. (only versions after 33 have a version, maybe drop this).

            // Files don't overwrite, the filename be altered with an index i.e., foobar.txt.1 if foobar.txt exists.
            File savedFile = fileService.saveFile(fileDto);
            if (savedFile != null) {
                // Save the file to the file repository and then record a reference in the database.
                try {
                    file.transferTo(new java.io.File(fileDto.getPathname() + "/" + savedFile.getFilename()));
                } catch (Exception e) {
                    LOG.error("Unable to persist file to disk: " + fileDto.getPathname() + "/" + savedFile.getFilename());
                    e.printStackTrace();
                    return "redirect:/files_add?error";
                }
            } else {
                // Something happened when trying to record the database record
                LOG.error("Failure to save file to database: " + file.getOriginalFilename());
                return "redirect:/files_add?error";
            }
        }
        return "redirect:/files/files_add?success";
    }

    // Handler method to delete a file from the system
    @PostMapping("/delete")
    public String deleteFile(@RequestParam(name = "id") Long id, Model model){
        List<FileDto> existingFile = fileService.findById(id);

        // If there is no file to delete, the delete sb reported as failure.
        if(existingFile == null || existingFile.size() == 0){
            return "redirect:/files?delete_failure";
        }

        java.io.File f = new java.io.File(existingFile.get(0).getPathname() + "/" + existingFile.get(0).getFilename());
        try {
            if(!f.delete()) {
                LOG.error("Error deleting file");
            }
        } catch(Exception e) {
            // If the file isn't there... keep going and remove the database entry.
            LOG.error("Error deleting file");
            e.printStackTrace();
        }

        fileService.deleteFileById(id);
        return "redirect:/files?delete_success";
    }


    // handler for add form
    @GetMapping("/files_add")
    public String showAddForm(Model model){
        // create model object to store form data
        FileDto file = new FileDto();
        model.addAttribute("file", file);

        return "file/files_add";
    }
}

