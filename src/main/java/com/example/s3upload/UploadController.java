package com.example.s3upload;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@Controller
public class UploadController {

    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }


    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        var files = uploadService.list();
        model.addAttribute("files", files);
        return "dashboard";
    }

    @PostMapping(value = "/upload")
    public String uploadFile(@RequestParam("file")MultipartFile multipartFile) {
        uploadService.uploadFile(multipartFile);
        return "redirect:dashboard";
    }
}
