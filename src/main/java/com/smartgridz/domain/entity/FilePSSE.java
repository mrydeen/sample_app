package com.smartgridz.domain.entity;

import com.smartgridz.controller.dto.FileDto;
import jakarta.persistence.*;

import java.io.FileNotFoundException;

@Entity
@DiscriminatorValue("PSSE_RAW")
public class FilePSSE extends File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="file_format_version", nullable=false)
    private final String fileFormatVersion;

    public FilePSSE(String fileName, String pathName, FileType fileType, String fileFormatVersion) {
        super(fileName, pathName, fileType);
        this.fileFormatVersion = fileFormatVersion;
    }

    public FilePSSE(FileDto fileDto) {
        super(fileDto);
        this.fileFormatVersion = fileDto.getFileFormatVersion();
    }

    // Would prefer to drop this constructor... framework complains, so here it is.
    @Deprecated
    public FilePSSE() throws FileNotFoundException {
        super(null, null, FileType.PSSE_RAW);
        fileFormatVersion = "-1";
    }

    @Override
    public String getFileFormatVersion() {
        return fileFormatVersion;
    }

    @Override
    public String toString() {
        return "FilePSSE{" +
                //"id=" + id +  // ID is autogenerated, it won't work in the test.
                "filename='" + super.getFilename() + '\'' +
                ", pathname='" + super.getPathname() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", casename='" + super.getCasename() + '\'' +
                ", fileType=" + "PSSE_RAW" +
                ", fileFormatVersion='" + fileFormatVersion + '\'' +
                ", createDate=" + super.getCreateDate() +
                ", size=" + super.getSizeInBytes() +
                ", addedBy=" + super.getAddedBy() +
                ", valid=" + super.isValid() +
                '}';
    }
}
