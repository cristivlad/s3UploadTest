package com.example.s3upload.excelparse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycOutDto {
    private String accountNumber;
    private String number;
    private String name;
    private String email;
    private String purposeOfAccount;
    private String estimatedMonthlyTransactionAmount;
    private String reasonForCashDeposit;
    private String createdOn;
    private String sourceOfIncome;

}
