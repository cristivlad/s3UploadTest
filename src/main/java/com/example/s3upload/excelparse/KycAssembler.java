package com.example.s3upload.excelparse;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KycAssembler {

    public KycData assembleEntity(KycInputDto dto) {
        return KycData.builder()
                .receivedFrom(dto.getReceivedFrom())
                .accountNumber(dto.getAccountNumber())
                .segment(dto.getSegment())
                .number(dto.getNumber())
                .typeformReference(dto.getTypeformReference())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .uaeResident(dto.getUaeResident())
                .nationality(dto.getNationality())
                .purposeOfAccount(dto.getPurposeOfAccount())
                .monthlyIncome(dto.getMonthlyIncome())
                .amountCashDeposit(dto.getAmountCashDeposit())
                .reasonCashDeposit(dto.getReasonCashDeposit())
                .expectedMonthlyTransactionAmount(dto.getExpectedMonthlyTransactionAmount())
                .eidFront(dto.getEidFront())
                .eidBack(dto.getEidBack())
                .passport(dto.getPassport())
                .visa(dto.getVisa())
                .sourceOfIncome(dto.getSourceOfIncome())
                .sponsorSourceOfIncome(dto.getSponsorSourceOfIncome())
                .salaryCertificate(dto.getSalaryCertificate())
                .companyTradeLicense(dto.getCompanyTradeLicense())
                .businessBankStatement(dto.getBusinessBankStatement())
                .personalBankStatement(dto.getPersonalBankStatement())
                .accommodationType(dto.getAccommodationType())
                .proofAddressDocument(dto.getProofAddressDocument())
                .titleDeed(dto.getTitleDeed())
                .address(dto.getAddress())
                .addressLine2(dto.getAddressLine2())
                .city(dto.getCity())
                .state(dto.getState())
                .postalCode(dto.getPostalCode())
                .country(dto.getCountry())
                .movedInDate(dto.getMovedInDate())
                .undertaking(dto.getUndertaking())
                .startDate(dto.getStartDate())
                .submitDate(dto.getSubmitDate())
                .status(dto.getStatus())
                .build();
    }

    public KycInputDto assembleDto(KycData data) {
        return KycInputDto.builder()
                .receivedFrom(data.getReceivedFrom())
                .accountNumber(data.getAccountNumber())
                .segment(data.getSegment())
                .number(data.getNumber())
                .typeformReference(data.getTypeformReference())
                .firstName(data.getFirstName())
                .lastName(data.getLastName())
                .email(data.getEmail())
                .uaeResident(data.getUaeResident())
                .nationality(data.getNationality())
                .purposeOfAccount(data.getPurposeOfAccount())
                .monthlyIncome(data.getMonthlyIncome())
                .amountCashDeposit(data.getAmountCashDeposit())
                .reasonCashDeposit(data.getReasonCashDeposit())
                .expectedMonthlyTransactionAmount(data.getExpectedMonthlyTransactionAmount())
                .eidFront(data.getEidFront())
                .eidBack(data.getEidBack())
                .passport(data.getPassport())
                .visa(data.getVisa())
                .sourceOfIncome(data.getSourceOfIncome())
                .sponsorSourceOfIncome(data.getSponsorSourceOfIncome())
                .salaryCertificate(data.getSalaryCertificate())
                .companyTradeLicense(data.getCompanyTradeLicense())
                .businessBankStatement(data.getBusinessBankStatement())
                .personalBankStatement(data.getPersonalBankStatement())
                .accommodationType(data.getAccommodationType())
                .proofAddressDocument(data.getProofAddressDocument())
                .titleDeed(data.getTitleDeed())
                .address(data.getAddress())
                .addressLine2(data.getAddressLine2())
                .city(data.getCity())
                .state(data.getState())
                .postalCode(data.getPostalCode())
                .country(data.getCountry())
                .movedInDate(data.getMovedInDate())
                .undertaking(data.getUndertaking())
                .startDate(data.getStartDate())
                .submitDate(data.getSubmitDate())
                .status(data.getStatus())
                .build();
    }

    public List<KycData> assembleEntities(List<KycInputDto> dtos) {
        return dtos.stream().map(this::assembleEntity).toList();
    }

    public List<KycInputDto> assembleDtos(List<KycData> entites) {
        return entites.stream().map(this::assembleDto).toList();
    }
}
