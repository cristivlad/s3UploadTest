package com.example.s3upload.excelparse;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class KycDataSpecification {
    public Predicate kycSearchPredicate(KycSearchCriteriaDto searchCriteria, Root<KycData> root, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (searchCriteria.getAccountNumber() != null) {
            predicates.add(cb.equal(root.get("accountNumber"), searchCriteria.getAccountNumber()));
        }
        if (searchCriteria.getMobileNo() != null) {
            predicates.add(cb.equal(root.get("mobileNo"), searchCriteria.getMobileNo()));
        }
        if (searchCriteria.getEmail() != null) {
            predicates.add(cb.equal(root.get("email"), searchCriteria.getEmail()));
        }
        if (searchCriteria.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), searchCriteria.getStatus()));
        }
        if (searchCriteria.getSourceOfIncome() != null) {
            predicates.add(cb.equal(root.get("sourceOfIncome"), searchCriteria.getSourceOfIncome()));
        }
        if (searchCriteria.getStartDate() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdOn"), searchCriteria.getStartDate()));
        }
        if (searchCriteria.getEndDate() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdOn"), searchCriteria.getEndDate()));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }
}