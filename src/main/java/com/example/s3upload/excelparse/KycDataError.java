package com.example.s3upload.excelparse;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_data_error")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KycDataError {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id;

    private String rowError;
    private String cause;
    private String accountNumber;
    private LocalDateTime createdOn;

}
