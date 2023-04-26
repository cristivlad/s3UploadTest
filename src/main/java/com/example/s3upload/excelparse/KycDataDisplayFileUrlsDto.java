package com.example.s3upload.excelparse;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KycDataDisplayFileUrlsDto {
    private String eidFront;
    private String eidBack;
    private String passport;
    private String visa;
    private String sponsorSourceOfIncome;
    private String salaryCertificate;
    private String companyTradeLicense;
    private String businessBankStatement;
    private String personalBankStatement;
    private String proofAddressDocument;
    private String titleDeed;
}
