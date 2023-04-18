package com.example.s3upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CustomerInput {

    private String name;
    private String email;
    private String phone;
    private String address;
}
