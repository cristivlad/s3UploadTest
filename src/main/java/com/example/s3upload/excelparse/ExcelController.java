package com.example.s3upload.excelparse;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationTargetException;

@RestController
public class ExcelController {

    private final ExcelService excelService;

    public ExcelController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @PostMapping("/load-excel")
    public ResponseEntity<Void> loadExcel(@RequestBody MultipartFile file) {
        excelService.loadExcel(file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/accounts/search")
    public ResponseEntity<Page<KycOutDto>> searchKyc(
            KycSearchCriteriaDto searchCriteria,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo) {

        var kycPage = excelService.searchKyc(searchCriteria, pageSize, pageNo);

        return ResponseEntity.ok(kycPage);
    }

    @DeleteMapping("/accounts/{accountNumber}")
    public ResponseEntity<Void> removeAccount(@PathVariable String accountNumber) {
        excelService.removeAccount(accountNumber);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value ="/accounts/{accountNumber}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateAccount(@PathVariable String accountNumber, KycUpdateDto kycUpdateDto) {
        excelService.updateAccount(accountNumber, kycUpdateDto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/accounts/{accountNumber}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable String accountNumber, @RequestParam String status) {
        excelService.updateStatus(accountNumber, status);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/accounts/{accountNumber}/files")
    public ResponseEntity<Void> removeFile(@PathVariable String accountNumber, @RequestParam String name) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        excelService.removeFile(accountNumber, name);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/accounts/{accountNumber}/move-files")
    public ResponseEntity<Void> moveFilesToBank(@PathVariable String accountNumber) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        excelService.moveFilesToBank(accountNumber);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/accounts/{accountNumber}/generate-urls")
    public ResponseEntity<KycDataDisplayFileUrlsDto> generateUrls(@PathVariable String accountNumber) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        var urls = excelService.generateUrls(accountNumber);
        return ResponseEntity.ok(urls);
    }
}
