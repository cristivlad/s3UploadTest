package com.example.s3upload.excelparse;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.login.AccountNotFoundException;

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

    @GetMapping("/search")
    public ResponseEntity<Page<KycOutDto>> searchKyc(
            KycSearchCriteriaDto searchCriteria,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "pageNo", defaultValue = "0") int pageNo) {

        var kycPage = excelService.searchKyc(searchCriteria, pageSize, pageNo);

        return ResponseEntity.ok(kycPage);
    }

    @DeleteMapping("/remove-account/{accountNumber}")
    public ResponseEntity<Void> removeAccount(@PathVariable String accountNumber) throws AccountNotFoundException {
        excelService.removeAccount(accountNumber);
        return ResponseEntity.ok().build();
    }
}
