package com.example.s3upload.excelparse;

import com.example.s3upload.S3Utils;
import jakarta.transaction.Transactional;
import org.apache.commons.beanutils.BeanAccessLanguageException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;

@Service
public class ExcelService {
    private final ExcelRepository excelRepository;
    private final KycDataErrorRepository kycDataErrorRepository;
    private final KycAssembler assembler;
    private final KycDataSpecification kycDataSpecification;

    private final S3Utils s3Utils;

    public ExcelService(ExcelRepository excelRepository, KycDataErrorRepository kycDataErrorRepository, KycAssembler assembler, KycDataSpecification kycDataSpecification, S3Utils s3Utils) {
        this.excelRepository = excelRepository;
        this.kycDataErrorRepository = kycDataErrorRepository;
        this.assembler = assembler;
        this.kycDataSpecification = kycDataSpecification;
        this.s3Utils = s3Utils;
    }

    @Transactional
    public void loadExcel(MultipartFile file) {
        try (var fis = new BufferedInputStream(file.getInputStream());
             var workbook = new XSSFWorkbook(fis)) {
            var sheet = workbook.getSheetAt(0);
            List<KycInputDto> inputDtoList = new ArrayList<>(sheet.getLastRowNum());

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                var row = sheet.getRow(rowIndex);
                var accountNumber = getAccountNumber(row);
                var movedInDate = convertToLocalDateTime(row, 36).orElse(null);
                var startDate = convertToLocalDateTime(row, 38).orElse(null);
                var submitDate = convertToLocalDateTime(row, 39).orElse(null);
                if (StringUtils.isBlank(accountNumber) || Objects.isNull(startDate) || Objects.isNull(submitDate)) {
                    continue;
                }
                constructObjectAndAddToList(inputDtoList, row, accountNumber, movedInDate, startDate, submitDate);
            }
            List<KycInputDto> validDataList = verifyDuplicateAccountNumbers(inputDtoList, file.getName());
            if (!CollectionUtils.isEmpty(validDataList)) {
                excelRepository.saveAll(assembler.assembleEntities(validDataList));
            }
        } catch (IOException e) {
            kycDataErrorRepository.save(createKycDataError(file.getName(), e.getMessage(), null));
        }
    }

    private String getAccountNumber(XSSFRow row) {
        try {
            return getCellValue(row, 1).toString().split("[.]")[0];
        } catch (AccountNumberNullException e) {
            kycDataErrorRepository.save(createKycDataError(String.valueOf(e.getRowNum()), e.getMessage(), row.getSheet().getSheetName()));
            return "";
        }
    }

    private void constructObjectAndAddToList(List<KycInputDto> inputDtoList, XSSFRow row, String accountNumber, LocalDateTime movedInDate, LocalDateTime startDate, LocalDateTime submitDate) {
        inputDtoList.add(KycInputDto.builder()
                .receivedFrom(getCellValue(row, 0).toString())
                .accountNumber(accountNumber)
                .segment(getCellValue(row, 2).toString())
                .number(getCellValue(row, 3).toString())
                .typeformReference(getCellValue(row,4).toString())
                .firstName(getCellValue(row, 5).toString())
                .lastName(getCellValue(row, 6).toString())
                .email(getCellValue(row, 7).toString())
                .uaeResident(Boolean.valueOf(getCellValue(row, 8).toString()))
                .nationality(getCellValue(row, 9).toString())
                .purposeOfAccount(getCellValue(row, 10).toString())
                .monthlyIncome(getCellValue(row,12).toString())
                .amountCashDeposit(getCellValue(row, 13).toString())
                .reasonCashDeposit(getCellValue(row, 14).toString())
                .expectedMonthlyTransactionAmount(getCellValue(row, 16).toString())
                .eidFront(getCellValue(row, 17).toString())
                .eidBack(getCellValue(row, 18).toString())
                .passport(getCellValue(row, 19).toString())
                .visa(getCellValue(row, 20).toString())
                .sourceOfIncome(getCellValue(row, 21).toString())
                .sponsorSourceOfIncome(getCellValue(row, 22).toString())
                .salaryCertificate(getCellValue(row, 23).toString())
                .companyTradeLicense(getCellValue(row, 24).toString())
                .businessBankStatement(getCellValue(row, 25).toString())
                .personalBankStatement(getCellValue(row, 26).toString())
                .accommodationType(getCellValue(row, 27).toString())
                .proofAddressDocument(getCellValue(row, 28).toString())
                .titleDeed(getCellValue(row, 29).toString())
                .address(getCellValue(row, 30).toString())
                .addressLine2(getCellValue(row, 31).toString())
                .city(getCellValue(row, 32).toString())
                .state(getCellValue(row, 33).toString())
                .postalCode(getCellValue(row, 34).toString())
                .country(getCellValue(row, 35).toString())
                .movedInDate(movedInDate)
                .undertaking(getCellValue(row, 37).toString())
                .startDate(startDate)
                .submitDate(submitDate)
                .status("WAITING FOR VALIDATION")
                .build());
    }

    private Optional<LocalDateTime> convertToLocalDateTime(XSSFRow row, int cellIndex) {
        Date inputDate = extractDateValue(row, cellIndex);
        return Optional.ofNullable(inputDate)
                .map(date -> LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
    }

    private Object getCellValue(XSSFRow row, int cellIndex) {
        Object o = extractCellValue(row, cellIndex);
        switch (cellIndex) {
            case 0, 12, 13, 16, 34 -> {
                return o.toString().split("[.]")[0];
            }
            case 1 -> {
                if (!StringUtils.isBlank(o.toString())) {
                    return o.toString();
                } else {
                    throw new AccountNumberNullException("Account number must not be null!", row.getRowNum());
                }
            }
            case 8 -> {
                return "1.0".equals(o.toString());
            }
            case 10 -> {
                return StringUtils.isBlank(o.toString()) ?
                        extractCellValue(row, 11).toString() : o.toString();
            }
            case 14 -> {
                return StringUtils.isBlank(o.toString()) ?
                        extractCellValue(row, 15).toString() : o.toString();
            }
            default -> {
                return o;
            }
        }
    }

    private Date extractDateValue(XSSFRow row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, CREATE_NULL_AS_BLANK);
        if (cell.getCellType() == CellType.STRING) {
            String date = cell.getStringCellValue();
            var formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            try {
                return formatter.parse(date);
            } catch (ParseException e) {
                kycDataErrorRepository.save(createKycDataError(String.valueOf(row.getCell(1)), e.getMessage(), row.getSheet().getSheetName()));
                return null;
            }
        }
        return cell.getDateCellValue();
    }
    private Object extractCellValue(XSSFRow row, int cellIndex) {
        Cell cell = row.getCell(cellIndex, CREATE_NULL_AS_BLANK);

        switch (cell.getCellType()) {
            case STRING -> { return cell.getStringCellValue(); }
            case NUMERIC -> { return cell.getNumericCellValue(); }
            case BOOLEAN -> { return cell.getBooleanCellValue(); }
            case FORMULA -> { return cell.getCellFormula(); }
            default -> { return ""; }
        }
    }

    private List<KycInputDto> verifyDuplicateAccountNumbers(List<KycInputDto> inputDtoList, String filename) {
        List<String> strings = excelRepository.checkAccountNumberExists(inputDtoList.stream().map(KycInputDto::getAccountNumber).distinct().toList());
        List<KycDataError> duplicateAccountsOnFileErrors = checkDuplicateAccountsOnFile(inputDtoList.stream().map(KycInputDto::getAccountNumber).toList(), filename);
        List<KycDataError> accountNumberAlreadyExists = strings.stream()
                .map(account -> createKycDataError(account, "Account number already exists", filename)).toList();

        List<KycDataError> finalErrors = Stream.concat(duplicateAccountsOnFileErrors.stream(), accountNumberAlreadyExists.stream()).toList();

        kycDataErrorRepository.saveAll(finalErrors);
        return inputDtoList.stream().distinct().filter(kycInputDto -> !strings.contains(kycInputDto.getAccountNumber())).toList();
    }

    private List<KycDataError> checkDuplicateAccountsOnFile(List<String> accountNumbers, String filename) {
        Map<String, Long> countsByAccountNumber = accountNumbers.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return countsByAccountNumber.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .map(entry -> createKycDataError(entry.getKey(), "Duplicate account number on file", filename))
                .collect(Collectors.toList());
    }

    private KycDataError createKycDataError(String accountNumber, String cause, String filename) {
        KycDataError error = new KycDataError();
        error.setAccountNumber(accountNumber);
        error.setCause(cause);
        error.setCreatedOn(LocalDateTime.now());
        error.setFilename(filename);
        return error;
    }

    public Page<KycOutDto> searchKyc(KycSearchCriteriaDto searchCriteria, int pageSize, int pageNo) {
        Page<KycData> kycPage = excelRepository.findAll((root, query, cb) -> kycDataSpecification.kycSearchPredicate(searchCriteria, root, cb), PageRequest.of(pageNo, pageSize));

        return kycPage.map(kycEntity ->
                KycOutDto.builder()
                        .accountNumber(kycEntity.getAccountNumber())
                        .number(kycEntity.getNumber())
                        .name(kycEntity.getFirstName() + " " + kycEntity.getLastName())
                        .email(kycEntity.getEmail())
                        .purposeOfAccount(kycEntity.getPurposeOfAccount())
                        .estimatedMonthlyTransactionAmount(kycEntity.getExpectedMonthlyTransactionAmount())
                        .reasonForCashDeposit(kycEntity.getReasonCashDeposit())
//                        .createdOn(kycEntity.getCreatedOn())
                        .sourceOfIncome(kycEntity.getSourceOfIncome())
                        .build());
    }

    @Transactional
    public void removeAccount(String accountNumber) {
        var kycData = excelRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new DataNotFoundException("Account not found"));

        excelRepository.deleteById(kycData.getId());
    }

    @Transactional
    public KycOutDto updateAccount(String accountNumber, KycUpdateDto kycUpdateDto) {

        var kycData = excelRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new DataNotFoundException("Account not found"));

        Field[] fields = kycUpdateDto.getClass().getDeclaredFields();
        List<Field> fieldList = Arrays.stream(fields).filter(field -> {
            try {
                field.setAccessible(true);
                return field.get(kycUpdateDto) != null;
            } catch (IllegalAccessException e) {
                return false;
            }
        }).collect(Collectors.toList());

        for (Field field : fieldList) {
            try {
                field.setAccessible(true);
                if (Arrays.stream(KycRemediationFiles.values()).anyMatch(val -> val.getValue().equals(field.getName()))) {
                    String pictureUrl = s3Utils.uploadMultipartFile((MultipartFile) field.get(kycUpdateDto), "", kycData.getAccountNumber(), "kyc-remediation", field.getName());

                    Field pictureUrlFieldObject = kycData.getClass().getDeclaredField(field.getName());
                    pictureUrlFieldObject.setAccessible(true);
                    pictureUrlFieldObject.set(kycData, pictureUrl);
                    pictureUrlFieldObject.setAccessible(false);
                } else {
                    Field kycInfoField = kycData.getClass().getDeclaredField(field.getName());
                    kycInfoField.setAccessible(true);
                    if ("uaeResident".equals(field.getName())) {
                        kycInfoField.set(kycData, Boolean.valueOf(field.get(kycUpdateDto).toString()));
                    } else {
                        kycInfoField.set(kycData, field.get(kycUpdateDto)) ;
                    }
                    kycInfoField.setAccessible(false);
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }

        excelRepository.save(kycData);

        return null;
    }

    public void updateStatus(String accountNumber, String status) {
        var kycData = excelRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new DataNotFoundException("Account not found"));

        kycData.setStatus(status);
        excelRepository.save(kycData);
    }

    public void removeFile(String accountNumber, String name) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var kycData = excelRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new DataNotFoundException("Account not found"));
        Class<?> returnedObjectType = kycData.getClass();
        Method getterMethod = returnedObjectType.getMethod("get" + name.substring(0, 1).toUpperCase() + name.substring(1));
        Method setterMethod = returnedObjectType.getMethod("set" + name.substring(0, 1).toUpperCase() + name.substring(1), String.class);
        Object propertyValue = getterMethod.invoke(kycData);

        s3Utils.deleteFile(propertyValue.toString());
        setterMethod.invoke(kycData, "");

        excelRepository.save(kycData);
    }

    public void moveFilesToBank(String accountNumber) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String sourceBucket = "testjavacode1";
        String destinationBucket = "newtestbucketlikebank";
        var kycData = excelRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new DataNotFoundException("Account not found"));

        Field[] fields = kycData.getClass().getDeclaredFields();
        for (var field : fields) {
            field.setAccessible(true);
            if (Arrays.stream(KycRemediationFiles.values()).anyMatch(val -> val.getValue().equals(field.getName()))) {
                String fileUrl = BeanUtils.getProperty(kycData, field.getName());
                if (StringUtils.isNotBlank(fileUrl)) {
                    var slashIndex = fileUrl.lastIndexOf('/');
                    var filename = StringUtils.isBlank(fileUrl) ? "" : fileUrl.substring(slashIndex + 1);

                    var destinationFileKey = "kyc-remediation/" + accountNumber + "/" + filename;

                    s3Utils.moveFile(sourceBucket, fileUrl, destinationBucket, destinationFileKey);
                }
            }
        }
    }

    public KycDataDisplayFileUrlsDto generateUrls(String accountNumber) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        var kycData = excelRepository.findByAccountNumber(accountNumber).orElseThrow(() -> new DataNotFoundException("Account not found"));
        var returnedObject = new KycDataDisplayFileUrlsDto();
        var fields = returnedObject.getClass().getDeclaredFields();

        for (var field : fields) {
            if (Arrays.stream(KycRemediationFiles.values()).anyMatch(val -> val.getValue().equals(field.getName()))) {
                    var fileUrl = BeanUtils.getProperty(kycData, field.getName());
                    if (StringUtils.isNotBlank(fileUrl)) {
                        var slashIndex = fileUrl.lastIndexOf('/');
                        var filename = StringUtils.isBlank(fileUrl) ? "" : fileUrl.substring(slashIndex + 1);
                        var url = s3Utils.generatePresignedUrl("", accountNumber, "kyc-remediation", filename);

                        BeanUtils.setProperty(returnedObject, field.getName(), url);
                    }
            }
        }
        return returnedObject;
    }
}
