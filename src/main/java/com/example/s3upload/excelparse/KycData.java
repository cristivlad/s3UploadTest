package com.example.s3upload.excelparse;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "kyc_data")
@Getter
@Service
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KycData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String receivedFrom;
    private String accountNumber;
    private String segment;
    private String number;
    private String typeformReference;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean uaeResident;
    private String nationality;
    private String purposeOfAccount;
    private String monthlyIncome;
    private String amountCashDeposit;
    private String reasonCashDeposit;
    private String expectedMonthlyTransactionAmount;
    private String eidFront;
    private String eidBack;
    private String passport;
    private String visa;
    private String sourceOfIncome;
    private String sponsorSourceOfIncome;
    private String salaryCertificate;
    private String companyTradeLicense;
    private String businessBankStatement;
    private String personalBankStatement;
    private String accommodationType;
    private String proofAddressDocument;
    private String titleDeed;
    private String address;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private LocalDateTime movedInDate;
    private String undertaking;
    private LocalDateTime startDate;
    private LocalDateTime submitDate;
    private String status;
}
