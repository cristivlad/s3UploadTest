package com.example.s3upload;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomersService {
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(CustomersService.class);
    private final CustomersRepository customersRepository;

    public CustomersService(CustomersRepository customersRepository) {
        this.customersRepository = customersRepository;
    }

    public void saveCustomer(CustomerInput inputData) {
        Customers customers = customersRepository.findByName(inputData.getName()).orElseThrow(() -> new DataNotFoundException("Customer not found"));

        logger.info("Customer found: {}", customers.getId());
        Customers customerToBeSaved = Customers.builder()
                .name(inputData.getName())
                .email(inputData.getEmail())
                .phone(inputData.getPhone())
                .address(inputData.getAddress())
                .build();

        customersRepository.save(customerToBeSaved);

    }
}
