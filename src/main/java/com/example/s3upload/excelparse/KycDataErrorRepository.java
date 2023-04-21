package com.example.s3upload.excelparse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KycDataErrorRepository extends JpaRepository<KycDataError, Long> {
}
