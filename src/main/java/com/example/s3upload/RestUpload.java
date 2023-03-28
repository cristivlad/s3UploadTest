package com.example.s3upload;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class RestUpload {

    public RestUpload(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    private final UploadService uploadService;

    @PostMapping("/uploadRest/files")
    public ResponseEntity<Void> upload(@RequestParam("files") MultipartFile[] files, @RequestParam("name") String name) {
        uploadService.uploadFiles(files, name);
        return ResponseEntity.ok().build();
    }
}
