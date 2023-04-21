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

    @Column(name = "receive_from")
    private String receivedFrom;
    @Column(name = "account_number")
    private String accountNumber;
    @Column(name = "segment")
    private String segment;
    private String number;
    @Column(name = "typeform_reference")
    private String typeformReference;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String email;
    @Column(name = "uae_resident")
    private Boolean uaeResident;
    private String nationality;
    @Column(name = "purpose_of_account")
    private String purposeOfAccount;
    @Column(name = "monthly_income")
    private String monthlyIncome;
    @Column(name = "amount_cash_deposit")
    private String amountCashDeposit;
    @Column(name = "reason_cash_deposit")
    private String reasonCashDeposit;
    @Column(name = "expected_monthly_transaction_amount")
    private String expectedMonthlyTransactionAmount;
    @Column(name = "eid_front")
    private String eidFront;
    @Column(name = "eid_back")
    private String eidBack;
    private String passport;
    private String visa;
    @Column(name = "source_of_income")
    private String sourceOfIncome;
    @Column(name = "sponsor_source_of_income")
    private String sponsorSourceOfIncome;
    @Column(name = "salary_certificate")
    private String salaryCertificate;
    @Column(name = "company_trade_license")
    private String companyTradeLicense;
    @Column(name = "business_bank_statement")
    private String businessBankStatement;
    @Column(name = "personal_bank_statement")
    private String personalBankStatement;
    @Column(name = "accommodation_type")
    private String accommodationType;
    @Column(name = "proof_address_document")
    private String proofAddressDocument;
    @Column(name = "title_deed")
    private String titleDeed;
    private String address;
    @Column(name = "address_line_2")
    private String addressLine2;
    private String city;
    private String state;
    @Column(name = "postal_code")
    private String postalCode;
    private String country;
    @Column(name = "moved_in_date")
    private LocalDateTime movedInDate;
    private String undertaking;
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "submit_date")
    private LocalDateTime submitDate;
    private String status;
}
