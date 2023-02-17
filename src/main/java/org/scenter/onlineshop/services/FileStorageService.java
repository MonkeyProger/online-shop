package org.scenter.onlineshop.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scenter.onlineshop.domain.FileDB;
import org.scenter.onlineshop.domain.ResponseFile;
import org.scenter.onlineshop.repo.FileRepo;
import org.scenter.onlineshop.repo.ResponseFileRepo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@AllArgsConstructor
public class FileStorageService {
    private FileRepo fileRepo;
    private ResponseFileRepo responseFileRepo;
    final String[] imageTypes = {"image/gif", "image/jpeg", "image/png"};

    public boolean checkImages(MultipartFile[] files){
        for (MultipartFile file : files) {
            if (Arrays.stream(imageTypes).noneMatch(file.getContentType()::contains)) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    public FileDB store(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        FileDB FileDB = new FileDB(fileName, file.getContentType(), file.getBytes());

        return fileRepo.save(FileDB);
    }

    public List<FileDB> saveFilesDB(MultipartFile[] files) throws RuntimeException{
        List<FileDB> filesDB = new ArrayList<>();
        Arrays.stream(files).forEach(file -> {
            try {
                filesDB.add(store(file));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return filesDB;
    }

    public FileDB getFile(String id) {
        return fileRepo.findById(id).get();
    }

    public Stream<FileDB> getAllFiles() {
        return fileRepo.findAll().stream();
    }

    @Transactional
    public ResponseFile saveResponsefile(FileDB file){
        String fileDownloadUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/stock/files/")
                .path(file.getId())
                .toUriString();

        return responseFileRepo.save(new ResponseFile(
                file.getId(),
                file.getName(),
                fileDownloadUri,
                file.getType(),
                file.getData().length));
    }
}
