package com.example.s3upload.excelparse;

public class AccountNumberNullException extends RuntimeException {
    private final int rowNum;

    public AccountNumberNullException(String message, int rowNum) {
        super(message);
        this.rowNum = rowNum;
    }

    public int getRowNum() {
        return rowNum;
    }
}
