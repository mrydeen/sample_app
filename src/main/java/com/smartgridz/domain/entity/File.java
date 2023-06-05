package com.smartgridz.domain.entity;

import com.smartgridz.controller.dto.FileDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

/**
 * This class represents a file that has been imported into this application.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="file_type")
@Table(name="files")
public abstract class File
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="filename", nullable=false)
    private String filename;

    // Keeping pathname because we may want to archive to a new root.
    @Column(name="pathname")
    private String pathname;

    @Column(name="description")
    private String description;

    /**
     * JPA needs a discriminator to identify the subclass, in this case file_type.  file_type is autofilled by JPA,
     * see FilePSSE.java.  If we switch to JDBC rowmappers, uncomment this.
    @Setter(AccessLevel.PRIVATE)
    @Column(name="file_type", nullable = false)
    private FileType fileType;
     */

    @Setter(AccessLevel.PRIVATE)
    @Column(name="create_date", nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name="size_in_bytes")
    private Long sizeInBytes;

    // relates to user.id, many to one.
    @Setter(AccessLevel.PRIVATE)
    @JoinColumn(name = "added_by", nullable=false )  // TODO: this should be a service that returns the active session user id.  -FRITZ
    private Long addedBy;

    // Minimal constructor
    public File(String filename, String pathname, FileType fileType) {
        this.filename = filename;
        this.pathname = pathname;
        this.addedBy = 1L;   // TODO: This should be a service call to get the user id from the session.  -FRITZ
        this.createDate = new Date();

        java.io.File f = new java.io.File(pathname + "/" + filename);
        if(f.isFile())
            this.sizeInBytes = f.length();
    }

    // DTO constructor to avoid duplication
    public File(FileDto fileDto) {
        this.filename    = fileDto.getFilename();
        this.pathname    = fileDto.getPathname();
        this.description = fileDto.getDescription();
        this.addedBy     = fileDto.getAddedBy();
        this.createDate  = fileDto.getCreateDate();
        this.sizeInBytes = fileDto.getSizeInBytes();
    }

    // Methods for the subclasses
    public abstract String getFileFormatVersion();  // Leave this as a string to deal with version inconsistencies between file formats, for now.

    @Override
    public String toString() {
        return "File{" +
                "filename='" + filename + '\'' +
                ", pathname='" + pathname + '\'' +
                ", description='" + description + '\'' +
                ", createDate=" + createDate +
                ", sizeInBytes=" + sizeInBytes +
                ", addedBy=" + addedBy +
                '}';
    }
}
