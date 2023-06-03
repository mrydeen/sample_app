package com.smartgridz.dao;

import com.smartgridz.domain.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * This interface is used to get access to the files
 *
 * It works in conjunction with the JPA.  By extending the JpaRepository
 * it will actually handle the CRUD operations.
 */
public interface FileDao extends JpaRepository<File, Long> {
    File findByPathnameAndFilename(String pathname, String filename);
}
