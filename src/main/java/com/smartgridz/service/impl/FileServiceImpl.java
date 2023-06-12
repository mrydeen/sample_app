package com.smartgridz.service.impl;

import com.smartgridz.controller.dto.FileDto;
import com.smartgridz.dao.FileDao;
import com.smartgridz.domain.entity.File;
import com.smartgridz.domain.entity.FilePSSE;
import com.smartgridz.domain.entity.FileType;
import com.smartgridz.domain.entity.User;
import com.smartgridz.service.FileService;
import com.smartgridz.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {
    private static final Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);

    @Autowired
    UserService userService;

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
    public List<FileDto> findById(Long id) {
        Optional<File> files = fileDao.findById(id);
        return files.stream()
                .map((file) -> mapToFileDto(file))
                .collect(Collectors.toList());
    }

    @Override
    public List<FileDto> findAllFiles() {
        List<File> files = fileDao.findAll();

        return files.stream()
                    .map((file) -> mapToFileDto(file))
                    .collect(Collectors.toList());
    }

    @Override
    public void saveFile(FileDto fileDtoIn) {

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

        File file = new FilePSSE(fileDto);
        fileDao.save(file);
    }

    @Override
    public void deleteFileById(Long id) {
        fileDao.deleteById(id);
    }

    @Override
    public void deleteAllFiles() {
        fileDao.deleteAll();
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

    /**
     * Get the authenticated login name then look up the name to get the ID.
     * @param login
     * @return ID of the login user
     */
    @Override
    public Long getUserId() {

        // Tests don't have this context
        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            User user = userService.findUserByLogin(SecurityContextHolder.getContext().getAuthentication().getName());
            if(user != null) {
                return user.getId();
            }
        }
        return null;
    }
}
