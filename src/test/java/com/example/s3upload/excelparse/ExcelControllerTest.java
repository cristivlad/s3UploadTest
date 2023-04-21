package com.example.s3upload.excelparse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ExcelControllerTest {

    @InjectMocks
    private ExcelController excelController;
    @Mock
    private ExcelService excelService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(excelController).build();
    }

    @Test
    void loadExcel_200() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "text/plain", "foo,bar".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.multipart("/load-excel")
                        .file(file))
                .andExpect(status().isOk());

        verify(excelService).loadExcel(file);
    }

    @Test
    void searchKyc_shouldReturn200_whenAllGood() throws Exception {
        int pageNo = 0;
        int pageSize = 10;
        mockMvc.perform(get("/search?pageNo=" + pageNo + "&pageSize=" + pageSize)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(excelService).searchKyc(any(KycSearchCriteriaDto.class), anyInt(), anyInt());
    }

    @Test
    void searchKyc_returnsPageOfKycOutDtos() {
        // Arrange
        KycSearchCriteriaDto searchCriteria = new KycSearchCriteriaDto();
        int pageSize = 10;
        int pageNo = 0;

        List<KycOutDto> kycOutDtoList = new ArrayList<>();
        kycOutDtoList.add(new KycOutDto());
        kycOutDtoList.add(new KycOutDto());
        Page<KycOutDto> kycPage = new PageImpl<>(kycOutDtoList);

        when(excelService.searchKyc(searchCriteria, pageSize, pageNo)).thenReturn(kycPage);

        // Act
        ResponseEntity<Page<KycOutDto>> response = excelController.searchKyc(searchCriteria, pageSize, pageNo);

        // Assert
        assertEquals(kycPage, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}