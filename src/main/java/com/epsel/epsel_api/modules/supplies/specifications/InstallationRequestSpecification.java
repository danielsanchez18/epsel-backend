package com.epsel.epsel_api.modules.supplies.specifications;

import com.epsel.epsel_api.modules.supplies.entities.InstallationRequest;
import com.epsel.epsel_api.modules.supplies.enums.InstallationRequestStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class InstallationRequestSpecification {

    public static Specification<InstallationRequest> search(
            String search,
            InstallationRequestStatus status,
            String zoneName
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isFalse(root.get("deleted")));

            if (search != null && !search.isBlank()) {

                Join<Object, Object> customer = root.join("customer");
                Join<Object, Object> property = root.join("property");
                String like = "%" + search.toLowerCase() + "%";

                predicates.add(cb.or(
                        cb.like(cb.lower(customer.get("fullName")), like),
                        cb.like(cb.lower(customer.get("documentNumber")), like),
                        cb.like(cb.lower(property.get("address")), like))
                );
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (zoneName != null) {
                predicates.add(cb.equal(root.get("property").get("zone").get("name"), zoneName));
            }

            query.orderBy(cb.desc(root.get("createdAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}