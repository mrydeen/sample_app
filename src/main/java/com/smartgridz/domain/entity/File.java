package com.smartgridz.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class represents a file that has been imported into this application.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="files")
public class File
{
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", nullable=false)
    private String name;

    @Column(nullable=false)
    private String location;

    @Column(nullable=false)
    private String description;

    @Column(name="transfer_date", nullable=false)
    private String transferDate;

    @Column(name="size")
    private Long size;

    @JoinColumn(name = "added_by", nullable=false )
    private String addedBy;

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("\nFile:").append("\n");
        sb.append("    id          : ").append(id).append("\n");
        sb.append("    name        : ").append(name).append("\n");
        sb.append("    location    : ").append(location).append("\n");
        sb.append("    description : ").append(description).append("\n");
        sb.append("    transferDate: ").append(transferDate).append("\n");
        sb.append("    size        : ").append(size).append("\n");
        sb.append("    addedBy     : ").append(addedBy).append("\n");
        return sb.toString();
    }
}
