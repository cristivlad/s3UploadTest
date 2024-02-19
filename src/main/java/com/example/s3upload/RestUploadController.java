package com.example.s3upload;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class RestUploadController {

    public RestUploadController(UploadService uploadService, CustomersService customersService) {
        this.uploadService = uploadService;
        this.customersService = customersService;
    }

    private final UploadService uploadService;
    private final CustomersService customersService;

    @PostMapping("/uploadRest/files")
    public ResponseEntity<Void> upload(@RequestParam("files") MultipartFile[] files, @RequestParam("name") String name) {
        uploadService.uploadFiles(files, name);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/createCustomer")
    public ResponseEntity<Void> createCustomer(@RequestBody CustomerInput inputData) {
        customersService.saveCustomer(inputData);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/generate-url")
    public ResponseEntity<String> generateUrl(@RequestParam("filename") String filename) {
        return ResponseEntity.ok(uploadService.generateUrl(filename));
    }
}
