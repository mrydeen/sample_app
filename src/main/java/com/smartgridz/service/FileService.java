package com.smartgridz.service;

import com.smartgridz.controller.dto.FileDto;
import com.smartgridz.domain.entity.File;

import java.util.List;

public interface FileService {

    FileDto findByCanonicalFilename(String filename);

    List<FileDto> findById(Long id);

    List<FileDto> findAllFiles();

    File saveFile(FileDto file);

    void deleteFileById(Long id);

    void deleteAllFiles();

    Long getUserId();
}
