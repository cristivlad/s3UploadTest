package com.example.s3upload.springintegrationtest.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "sftp_config_lock")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SftpConfigLock {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name = "locked")
    private boolean locked;

    @Column(name = "lock_granted")
    private LocalDateTime lockGranted;

    @Column(name = "locked_by")
    private String lockedBy;

    @Column(name = "locked_for")
    private String lockedFor;
}
