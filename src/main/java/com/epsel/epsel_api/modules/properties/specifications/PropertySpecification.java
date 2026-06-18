package com.epsel.epsel_api.modules.properties.specifications;

import com.epsel.epsel_api.modules.properties.entities.Property;
import com.epsel.epsel_api.modules.properties.enums.PropertyType;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class PropertySpecification {

    public static Specification<Property> search(String search, PropertyType type, UUID customerId, java.time.LocalDateTime startDate, java.time.LocalDateTime endDate) {

        return (root, query, cb) -> {

            var predicate = cb.conjunction();

            predicate = cb.and(predicate, cb.isFalse(root.get("deleted")));

            if (search != null && !search.isBlank()) {

                String like = "%" + search.toLowerCase() + "%";

                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("cadastralCode")), like),
                        cb.like(cb.lower(root.get("address")), like),
                        cb.like(cb.lower(root.get("reference")), like))
                );
            }

            if (type != null) {
                predicate = cb.and(predicate, cb.equal(root.get("type"), type));
            }

            if (customerId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("customer").get("id"), customerId));
            }

            if (startDate != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }

            if (endDate != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }

            query.orderBy(cb.desc(root.get("createdAt")));

            return predicate;
        };
    }
}