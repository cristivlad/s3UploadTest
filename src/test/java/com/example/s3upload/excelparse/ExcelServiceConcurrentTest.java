package com.example.s3upload.excelparse;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newFixedThreadPool;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Sql(scripts = "classpath:test_data.sql")
@Testcontainers
class ExcelServiceConcurrentTest {

    @Autowired
    private ExcelService excelService;
    @Autowired
    private ExcelRepository excelRepository;
    @Autowired
    TransactionTemplate transactionTemplate;

    @Container
    private static MySQLContainer container = new MySQLContainer("mysql:8");

    @BeforeAll
    static void setUp() {
        container.start();
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @Test
    void initialTest() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        KycDataDisplayFileUrlsDto kycDataDisplayFileUrlsDto = excelService.generateUrls("11");

        assertNotNull(kycDataDisplayFileUrlsDto);
    }

    @Test
    void updateData_sameThread() {
        KycUpdateDto update1 = new KycUpdateDto();
        update1.setFirstName("Romania");
        excelService.updateAccount("11", update1);

        KycData accountNotFound = excelService.getAccountNotFound("11");
        assertEquals("Romania", accountNotFound.getFirstName());
    }

    @Test
    void updateData_oneThread() {
        KycUpdateDto update1 = new KycUpdateDto();
        update1.setFirstName("Romania");
        ExecutorService executor = newFixedThreadPool(1);

        CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> excelService.updateAccount("11", update1), executor);

        CompletableFuture.allOf(task1).join();
//        ResponseEntity<Void> voidResponseEntity = excelController.updateAccount("11", update1);

//        assertEquals(204, voidResponseEntity.getStatusCodeValue());
    }

    @Test
    void updateData_optimisticLocking() throws InterruptedException {
        KycUpdateDto update1 = new KycUpdateDto();
        update1.setFirstName("Romania");
        KycUpdateDto update2 = new KycUpdateDto();
        update2.setEmail("flo_flo_"+new Random().nextInt());
        final List<KycUpdateDto> updates = List.of(update1, update2);

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(16);

        for (int i = 0; i < 16; i++) {
            new Thread(() -> {
                awaitOnLatch(startLatch);
                try {
                    excelService.updateAccount("11", update2);
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }

        startLatch.countDown();
        awaitOnLatch(endLatch);

        //create a new transaction
//        transactionTemplate.executeWithoutResult(transactionStatus -> {
//            excelService.updateAccount("11", update1);
//
//        });
//        transactionTemplate.executeWithoutResult(transactionStatus -> {
//            excelService.updateAccount("11", update2);
//        });


//        CountDownLatch latch = new CountDownLatch(2);
//        final ExecutorService executor = newFixedThreadPool(updates.size());
//
//        CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> excelService.updateAccount("11", update1), executor);
//        CompletableFuture<Void> task2 = CompletableFuture.runAsync(() -> excelService.updateAccount("11", update2), executor);
//
//        CompletableFuture.allOf(task1, task2).join();

//        executor.execute(() -> {
//            try {
//                excelService.updateAccount("11", update1);
//            } finally {
//                latch.countDown();
//            }
//        });
//        executor.execute(() -> {
//            try {
//                excelService.updateAccount("11", update2);
//            } finally {
//                latch.countDown();
//            }
//        });
//
//        executor.shutdown();
//        executor.awaitTermination(1, TimeUnit.MINUTES);
//        latch.await();

        KycSearchCriteriaDto kycSearchCriteriaDto = new KycSearchCriteriaDto();
        kycSearchCriteriaDto.setAccountNumber("11");
        Page<KycOutDto> kycOutDtos = excelService.searchKyc(kycSearchCriteriaDto, 1, 0);
        assertEquals("11", kycOutDtos.getContent().get(0).getAccountNumber());
//        assertEquals("Romania", kycOutDtos.getContent().get(0).getName());
//        assertEquals("florin@florin.flo", kycOutDtos.getContent().get(0).getEmail());
        assertNotNull(kycOutDtos.getContent().get(0).getEmail());
    }

    protected void awaitOnLatch(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

}