package com.smartgridz.service;

import com.smartgridz.controller.dto.FileDto;

import java.util.List;

public interface FileService {

    FileDto findByCanonicalFilename(String filename);

    List<FileDto> findById(Long id);

    List<FileDto> findAllFiles();

    void saveFile(FileDto file);

    void deleteFileById(Long id);

    void deleteAllFiles();

    Long getUserId();
}
