package com.epsel.epsel_api.modules.configurations.specifications;

import com.epsel.epsel_api.modules.configurations.entities.ServiceFeeConfiguration;
import com.epsel.epsel_api.modules.configurations.enums.ServiceFeeType;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServiceFeeConfigurationSpecification {

    public static Specification<ServiceFeeConfiguration> search(
            UUID zoneId, ServiceFeeType feeType, Boolean active) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isFalse(root.get("deleted")));

            System.out.println("ServiceFeeConfigurationSpecification.search -> zoneId='" + zoneId + "', feeType='" + feeType + "', active='" + active + "'");

            if (zoneId != null) {
                predicates.add(cb.equal(root.get("zone").get("id"), zoneId));
                System.out.println("ServiceFeeConfigurationSpecification.search -> filtrando por zoneId");
            }

            if (feeType != null) {
                predicates.add(cb.equal(root.get("feeType"), feeType));
                System.out.println("ServiceFeeConfigurationSpecification.search -> filtrando por feeType=" + feeType);
            }

            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
                System.out.println("ServiceFeeConfigurationSpecification.search -> filtrando por active=" + active);
            }

            Predicate[] arr = predicates.toArray(new Predicate[0]);
            return cb.and(arr);
        };
    }
}