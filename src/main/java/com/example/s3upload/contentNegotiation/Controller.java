package com.example.s3upload.contentNegotiation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Controller {
    @GetMapping(value = "/salam", produces = {MediaType.APPLICATION_JSON_VALUE, "text/csv"})
    public ResponseEntity<CustomerList> getAll(HttpServletResponse response, HttpServletRequest request) {
        Customer customer1 = Customer.builder().name("John").surname("John").phoneNumber("123").build();
        Customer customer2 = Customer.builder().name("Marcel").surname("Ion").phoneNumber("1234123").build();
        Customer customer3 = Customer.builder().name("Salam").surname("Alina").phoneNumber("135623").build();
        Customer customer4 = Customer.builder().name("Alina").surname("PEtre").phoneNumber("125323").build();
        Customer customer5 = Customer.builder().name("Gica").surname("Hagi").phoneNumber("112").build();

        CustomerList customerList = new CustomerList();
        customerList.setList(List.of(customer1, customer2, customer3, customer4, customer5));

        if (request.getHeader("Accept").equals("text/csv")) {
            response.setHeader("Content-Disposition", "attachment; filename=customers.csv");
            response.setContentType("text/csv");
        }
        return ResponseEntity.ok(customerList);
    }
}
