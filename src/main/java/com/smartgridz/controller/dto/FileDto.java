package com.smartgridz.controller.dto;

import com.smartgridz.domain.entity.FileType;
import jakarta.persistence.Entity;
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

    public FileDto(FileDto fileDto) {
        this.filename = fileDto.getFilename();
        this.pathname = fileDto.getPathname();
        this.addedBy = fileDto.getAddedBy();
        this.description = fileDto.getDescription();
        this.sizeInBytes = fileDto.getSizeInBytes();
        this.fileFormatVersion = fileDto.getFileFormatVersion();
        this.createDate = fileDto.getCreateDate();
        this.fileType = fileDto.getFileType();
    }

    @Override
    public String toString() {
        return "FileDto{" +
                "id=" + id +
                ", filename='" + filename + '\'' +
                ", pathname='" + pathname + '\'' +
                ", description='" + description + '\'' +
                ", fileType=" + fileType +
                ", createDate=" + createDate +
                ", size=" + sizeInBytes +
                ", addedBy=" + addedBy +
                ", fileFormatVersion='" + fileFormatVersion + '\'' +
                '}';
    }
}
