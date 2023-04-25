package com.example.s3upload.excelparse;

public enum KycRemediationFiles {
    EID_FRONT("eidFront"),
    EID_BACK("eidBack"),
    PASSPORT("passport"),
    VISA("visa"),
    SPONSOR_SOURCE_OF_INCOME("sponsorSourceOfIncome"),
    SALARY_CERTIFICATE("salaryCertificate"),
    COMPANY_TRADE_LICENSE("companyTradeLicense"),
    BUSINESS_BANK_STATEMENT("businessBankStatement"),
    PERSONAL_BANK_STATEMENT("personalBankStatement"),
    PROOF_ADDRESS_DOCUMENT("proofAddressDocument"),
    TITLE_DEED("titleDeed");

    private String value;

    KycRemediationFiles(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
