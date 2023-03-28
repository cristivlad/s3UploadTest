package com.example.s3upload;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class UploadService {
    private static final String BUCKET_NAME = "testjavacode1";
    private final S3Utils s3Utils;
    private final FileMetaRepository fileMetaRepository;

    public UploadService(S3Utils s3Utils, FileMetaRepository fileMetaRepository) {
        this.s3Utils = s3Utils;
        this.fileMetaRepository = fileMetaRepository;
    }

    @Transactional
    public void uploadFiles(MultipartFile[] files, String name) {
        List<FileMeta> filesToUpload = new ArrayList<>();
        for (MultipartFile file : files) {
            S3FileInfo upload = upload(file);
            filesToUpload.add(new FileMeta(upload.getFilename(), upload.getPath(), "", name));
        }
        checkAndSaveToDb(filesToUpload);

    }

    @Transactional
    private void checkAndSaveToDb(List<FileMeta> filesToUpload) {
        Optional<FileMeta> byName = fileMetaRepository.findByName(filesToUpload.get(0).getName());
        String filenames = filesToUpload.stream().map(FileMeta::getFileName).reduce((s, s2) -> s + ", " + s2).get();
        String path = filesToUpload.stream().map(FileMeta::getFilePath).reduce((s, s2) -> s + ", " + s2).get();
        if (byName.isPresent()) {
            String addedFilename = filenames.concat(", " + byName.get().getFileName());
            String addedPath = path.concat(", " + byName.get().getFilePath());
             fileMetaRepository.updateFileNameAndPath(filesToUpload.get(0).getName(), addedFilename, addedPath);
        } else {
            fileMetaRepository.save(new FileMeta(filenames, path, "", filesToUpload.get(0).getName()));
        }
    }

    @Transactional
    private S3FileInfo upload(MultipartFile file) {
        if (file.isEmpty())
            throw new IllegalStateException("Cannot upload empty file");

        Map<String, String> metadata = new HashMap<>();
        metadata.put("Content-Type", file.getContentType());
        metadata.put("Content-Length", String.valueOf(file.getSize()));

        String path = String.format("%s", BUCKET_NAME);
        String filename = String.format("%s", file.getOriginalFilename());

        try {
            s3Utils.uploadFile(path, filename, Optional.of(metadata), file.getInputStream(), BUCKET_NAME);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to upload file", e);
        }

//        String link = s3Utils.getFileLocation(BUCKET_NAME, filename).toString();
        String link = s3Utils.generatePreSignedUrl(BUCKET_NAME, filename).toString();

        return new S3FileInfo(link, filename);
    }

    public void uploadFile(MultipartFile file) {
        S3FileInfo upload = upload(file);
        saveFileMeta(upload.getFilename(), upload.getPath(), "");
    }

    private void saveFileMeta(String filename, String link, String version) {
        fileMetaRepository.save(new FileMeta(filename, link, version));
    }

    public List<FileMeta> list() {
        List<FileMeta> metas = new ArrayList<>();
        fileMetaRepository.findAll().forEach(metas::add);
        return metas;
    }
}
