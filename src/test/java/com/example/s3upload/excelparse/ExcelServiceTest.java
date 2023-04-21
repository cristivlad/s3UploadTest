package com.example.s3upload.excelparse;

import com.amazonaws.util.IOUtils;
import com.example.s3upload.S3UploadApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExcelServiceTest {

    @InjectMocks
    private ExcelService excelService;
    @Mock
    private ExcelRepository excelRepository;
    @Mock KycDataErrorRepository kycDataErrorRepository;
    @Mock
    private KycAssembler assembler;

    @Test
    void loadExcel_allGood() throws IOException {
        try (InputStream is = ExcelServiceTest.class.getResourceAsStream("/testGood.xlsx");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            MultipartFile multipartFile = new MockMultipartFile("file",
                    "test.xlsx", "text/plain", IOUtils.toByteArray(is));
            excelService.loadExcel(multipartFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        verify(excelRepository).saveAll(Mockito.anyList());
    }

    @Test
    void loadExcel_testDuplicateValueInFile() {
        try (InputStream is = ExcelServiceTest.class.getResourceAsStream("/testDuplicateValueInFile.xlsx");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            MultipartFile multipartFile = new MockMultipartFile("file",
                    "test.xlsx", "text/plain", IOUtils.toByteArray(is));
            excelService.loadExcel(multipartFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        verify(kycDataErrorRepository).saveAll(Mockito.anyList());
        verify(excelRepository).saveAll(Mockito.anyList());
    }

    @Test
    void loadExcel_testDuplicateValueOnDb() {
        when(excelRepository.checkAccountNumberExists(anyList())).thenReturn(List.of("0188000495524"));
        try (InputStream is = ExcelServiceTest.class.getResourceAsStream("/testDuplicateValueInFile.xlsx");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            MultipartFile multipartFile = new MockMultipartFile("file",
                    "test.xlsx", "text/plain", IOUtils.toByteArray(is));
            excelService.loadExcel(multipartFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        verify(kycDataErrorRepository).saveAll(Mockito.anyList());
        verifyNoMoreInteractions(excelRepository);
    }
}