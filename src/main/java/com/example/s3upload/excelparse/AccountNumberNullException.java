package com.example.s3upload.excelparse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
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
