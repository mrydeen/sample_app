package com.smartgridz.service;

import com.smartgridz.controller.dto.FileDto;
import com.smartgridz.domain.entity.File;

import java.util.List;

public interface FileService {

    FileDto findByCanonicalFilename(String filename);
    File findByPathnameAndFilename(String pathname, String filename);

    List<FileDto> findById(Long id);

    List<FileDto> findAllFilesAsDto();

    void update(FileDto fileDto);

    File saveFile(FileDto file);

    void deleteFileById(Long id);

    void deleteAllFiles();

    void deleteByPathnameAndFilename(String pathname, String filename);
}
