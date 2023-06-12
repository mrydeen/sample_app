
package com.smartgridz.controller;

import com.smartgridz.controller.dto.FileDto;
import com.smartgridz.controller.dto.UserDto;
import com.smartgridz.domain.entity.File;
import com.smartgridz.domain.entity.FileType;
import com.smartgridz.service.FileService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/files")
public class FileController {

    private static final Logger LOG = LoggerFactory.getLogger(FileController.class);

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    // handler method to handle list of files
    @GetMapping()
    public String files(Model model){
        List<FileDto> files = fileService.findAllFiles();
        model.addAttribute("files", files);
        return "file/files";
    }

    // Handler method to handle file add form submit request
    @PostMapping("/add")
    public String saveFile(@Valid @ModelAttribute("user") FileDto fileDto,
                               BindingResult result,
                               Model model){

        if(result.hasErrors()){
            model.addAttribute("filename", fileDto);
            return "file/files_add";
        }

        //
        // TODO: once we figure out how to get the file, we will parse the file to fill out some of the properties.
        //
        LOG.error("FileController.saveFile(): POPULATING DEFAULT PROPERTIES FIXME!");
        fileDto.setPathname("/tmp");             // TODO: this needs to come from the file chooser.
        fileDto.setFileType(FileType.PSSE_RAW);  // TODO: This needs to be determined by scanning the file metadata.
        fileDto.setCreateDate(new Date());
        fileDto.setSize(20L);                    // TODO: This needs to be determined from the physical file.

        Long userId = fileService.getUserId();
        fileDto.setAddedBy(userId!=null ? userId : 0L);
        fileDto.setFileFormatVersion("33");      // TODO: This needs to be determined by scanning the file metadata.

        fileService.saveFile(fileDto);
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

