package com.example.s3upload.excelparse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycInputDto {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KycInputDto that = (KycInputDto) o;
        return Objects.equals(getAccountNumber(), that.getAccountNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAccountNumber());
    }
}
