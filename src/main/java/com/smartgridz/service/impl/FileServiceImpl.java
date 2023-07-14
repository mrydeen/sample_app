package com.smartgridz.service.impl;

import com.smartgridz.config.SystemOptionsService;
import com.smartgridz.controller.dto.FileDto;
import com.smartgridz.dao.FileDao;
import com.smartgridz.domain.entity.File;
import com.smartgridz.domain.entity.FilePSSE;
import com.smartgridz.domain.entity.FileType;
import com.smartgridz.service.FileService;
import com.smartgridz.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {
    private static final Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);

    @Autowired
    private SystemOptionsService systemOptionsService;

    @Autowired
    private UserService userService;

    private final FileDao fileDao;

    public FileServiceImpl(FileDao fileDao) {
        this.fileDao = fileDao;
    }

    @Override
    public FileDto findByCanonicalFilename(String canonicalFilename) {

        // all file names contain at least one slash
        int lastSlash = Math.max(canonicalFilename.lastIndexOf('\\'), canonicalFilename.lastIndexOf('/'));

        String pathname = canonicalFilename.substring(0, Math.max(lastSlash, 1));
        String filename = canonicalFilename.substring(lastSlash+1);

        File file = fileDao.findByPathnameAndFilename(pathname, filename);

        return (file != null) ? mapToFileDto(file) : null;
    }
    @Override
    public File findByPathnameAndFilename(String pathname, String filename) {

        return fileDao.findByPathnameAndFilename(pathname, filename);

    }


    @Override
    public List<FileDto> findById(Long id) {
        Optional<File> files = fileDao.findById(id);
        return files.stream()
                .map((file) -> mapToFileDto(file))
                .collect(Collectors.toList());
    }

    @Override
    public List<FileDto> findAllFilesAsDto() {
        List<File> files = fileDao.findAll();

        return files.stream()
                    .map((file) -> mapToFileDto(file))
                    .collect(Collectors.toList());
    }

    @Override
    public void update(FileDto fileDto) {
        // TODO: make sure the record exists before saving?
        Optional<File> files = fileDao.findById(fileDto.getId());
        List<File> x = files.stream().toList();
        File f = x.get(0);

        // Need to merge attributes or similar TODO
        f.setValid(fileDto.isValid());

        fileDao.save(f);
    }

    @Override
    public File saveFile(FileDto fileDtoIn) {

        FileDto fileDto = new FileDto(fileDtoIn);

        // If the filename already exists, change the name.
        // If foobar.psse doesn't exist when saved, foobar.psse is the File.filename().
        // If foobar.psse exists when foobar.psse is saved, foobar.psse.1 is the File.filename().
        String filename = fileDto.getFilename();
        int index = 0;

        File f = fileDao.findByPathnameAndFilename(fileDto.getPathname(), fileDto.getFilename());

        // Filename already exists, find a new one, holes get filled in for better or worse.
        while(f != null && f.getFilename().length() > 0) {
            index++;
            f = fileDao.findByPathnameAndFilename(fileDto.getPathname(), fileDto.getFilename() + "." + index);
        }
        // Update the filename
        if(index > 0) {
            fileDto.setFilename(filename + "." + index);
        }

        return fileDao.save(new FilePSSE(fileDto));
    }

    @Override
    public void deleteFileById(Long id) {
        fileDao.deleteById(id);
    }

    @Override
    public void deleteAllFiles() {
        fileDao.deleteAll();
    }

    @Override
    public void deleteByPathnameAndFilename(String pathname, String filename) {
        fileDao.deleteByPathnameAndFilename(pathname, filename);
    }

    private FileDto mapToFileDto(File file) {
        FileDto fileDto = new FileDto();
        fileDto.setId(file.getId());
        fileDto.setFilename(file.getFilename());
        fileDto.setPathname(file.getPathname());
        fileDto.setDescription(file.getDescription());
        fileDto.setCreateDate(file.getCreateDate());
        fileDto.setSizeInBytes(file.getSizeInBytes());
        fileDto.setAddedBy(file.getAddedBy());
        fileDto.setFileFormatVersion(file.getFileFormatVersion());

        // The Discriminator is the subclass
        // Add other file types here as they are implemented
        if(file instanceof FilePSSE)
            fileDto.setFileType(FileType.PSSE_RAW);

        return fileDto;
    }


}
