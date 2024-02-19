package com.example.s3upload.springintegrationtest;

import com.example.s3upload.springintegrationtest.dto.SftpConfigLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SFTPConfigLockRepository extends JpaRepository<SftpConfigLock, Long> {

    Optional<SftpConfigLock> findFirstByLockedFor(String lockedFor);

    List<SftpConfigLock> findAllByLockedFor(String lockedFor);
}
