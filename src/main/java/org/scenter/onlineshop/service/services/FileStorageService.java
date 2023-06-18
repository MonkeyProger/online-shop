package org.scenter.onlineshop.service.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scenter.onlineshop.domain.FileDB;
import org.scenter.onlineshop.domain.ProductFile;
import org.scenter.onlineshop.domain.ResponseFile;
import org.scenter.onlineshop.repo.FileRepo;
import org.scenter.onlineshop.repo.ProductFileRepo;
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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@AllArgsConstructor
public class FileStorageService {
    private FileRepo fileRepo;
    private ResponseFileRepo responseFileRepo;
    private ProductFileRepo productFileRepo;
    final String[] imageTypes = {"image/gif", "image/jpeg", "image/png"};

    protected boolean checkImages(MultipartFile[] files){
        for (MultipartFile file : files) {
            if (file.getContentType() == null || Arrays.stream(imageTypes).noneMatch(file.getContentType()::contains)) {
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

    protected FileDB getFile(String id) {
        Optional<FileDB> fileDB = fileRepo.findById(id);
        return fileDB.orElse(null);
    }

    protected Stream<FileDB> getAllFiles() {
        return fileRepo.findAll().stream();
    }

    protected void deleteResponseFiles(List<ResponseFile> responseFiles){
        if (responseFiles.isEmpty()) return;
        deleteFiles(responseFiles.stream().map(ResponseFile::getFileDBid).collect(Collectors.toList()));
        responseFileRepo.deleteAll(responseFiles);
    }

    protected void deleteProductFiles(List<ProductFile> productFiles){
        if (productFiles.isEmpty()) return;
        deleteFiles(productFiles.stream().map(ProductFile::getFileDBid).collect(Collectors.toList()));
        //responseFiles.forEach(file -> deleteFile(file.getFileDBid()));
        productFileRepo.deleteAll(productFiles);
    }

    @Transactional
    public void deleteFile(String fileId){
        fileRepo.deleteById(fileId);
    }

    @Transactional
    public void deleteFiles(List<String> fileId){
        fileRepo.deleteAllById(fileId);
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

    @Transactional
    public ProductFile saveProductFile(FileDB file){
        String fileDownloadUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/stock/files/")
                .path(file.getId())
                .toUriString();

        return productFileRepo.save(new ProductFile(
                file.getId(),
                file.getName(),
                fileDownloadUri,
                file.getType(),
                file.getData().length));
    }
}
