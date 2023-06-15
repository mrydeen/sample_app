package com.smartgridz.controller.dto;

import com.smartgridz.domain.entity.FileType;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {
    private Long id;

    @NotEmpty
    private String filename;

    //@NotEmpty
    private String pathname;

    private String description;

    private String casename;

    //@NotEmpty
    private FileType fileType;

    //@NotEmpty
    private Date createDate;

    //@NotEmpty
    private Long sizeInBytes;

    //@NotEmpty
    private Long addedBy;

    //@NotEmpty
    private String fileFormatVersion;

    //@NotEmpty
    private MultipartFile file;

    private boolean valid;

    @Override
    public String toString() {
        return "FileDto{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", pathname='" + pathname + '\'' +
                ", description='" + description + '\'' +
                ", casename='" + casename + '\'' +
                ", fileType=" + fileType +
                ", createDate=" + createDate +
                ", sizeInBytes=" + sizeInBytes +
                ", addedBy=" + addedBy +
                ", fileFormatVersion='" + fileFormatVersion + '\'' +
                ", file=" + file +
                ", invalid=" + valid +
                '}';
    }

    public FileDto(FileDto fileDto) {
        this.id = fileDto.getId();
        this.filename = fileDto.getFilename();
        this.pathname = fileDto.getPathname();
        this.addedBy = fileDto.getAddedBy();
        this.description = fileDto.getDescription();
        this.sizeInBytes = fileDto.getSizeInBytes();
        this.fileFormatVersion = fileDto.getFileFormatVersion();
        this.createDate = fileDto.getCreateDate();
        this.fileType = fileDto.getFileType();
        this.casename = fileDto.getCasename();
        this.file = fileDto.getFile();
        this.valid = fileDto.isValid();
    }

}
