package com.example.s3upload.excelparse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExcelRepository extends JpaRepository<KycData, Long>, JpaSpecificationExecutor<KycData> {

    @Query("SELECT k.accountNumber FROM KycData k WHERE k.accountNumber IN :accountNumbers")
    List<String> checkAccountNumberExists(List<String> accountNumbers);

    Optional<KycData> findByAccountNumber(String accountNumber);
}
