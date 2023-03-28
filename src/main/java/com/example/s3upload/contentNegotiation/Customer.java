package com.example.s3upload.contentNegotiation;

import com.opencsv.bean.CsvBindByName;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@Builder
@Setter
public class Customer {

    @CsvBindByName
    private String name;
    @CsvBindByName
    private String surname;
    @CsvBindByName
    private String phoneNumber;
}
