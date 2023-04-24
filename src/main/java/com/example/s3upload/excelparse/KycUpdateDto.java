package com.example.s3upload.excelparse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KycUpdateDto {

    private String receivedFrom;
    private String segment;
    private String number;
    private String firstName;
    private String lastName;
    private String email;
    private boolean uaeResident;
    private String nationality;
    private String purposeOfAccount;
    private String monthlyIncome;
    private String amountCashDeposit;
    private String reasonCashDeposit;
    private String expectedMonthlyTransactionAmount;
    private String sourceOfIncome;
    private String accommodationType;
    private String address;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private MultipartFile eidFront;
    private MultipartFile eidBack;
    private MultipartFile passport;
    private MultipartFile visa;
    private MultipartFile sponsorSourceOfIncome;
    private MultipartFile salaryCertificate;
    private MultipartFile companyTradeLicense;
    private MultipartFile businessBankStatement;
    private MultipartFile personalBankStatement;
    private MultipartFile proofAddressDocument;
    private MultipartFile titleDeed;
}
