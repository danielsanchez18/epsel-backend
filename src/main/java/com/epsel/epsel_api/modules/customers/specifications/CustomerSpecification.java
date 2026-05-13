package com.epsel.epsel_api.modules.customers.specifications;

import com.epsel.epsel_api.modules.customers.entities.Customer;
import com.epsel.epsel_api.modules.customers.enums.CustomerType;
import org.springframework.data.jpa.domain.Specification;

public class CustomerSpecification {

    public static Specification<Customer> search(
            String search,
            CustomerType type
    ) {

        return (root, query, cb) -> {

            var predicate = cb.conjunction();
            predicate = cb.and(predicate, cb.isFalse(root.get("deleted")));

            if (search != null && !search.isBlank()) {

                String like = "%" + search.toLowerCase() + "%";

                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("fullName")), like),
                        cb.like(cb.lower(root.get("documentNumber")), like),
                        cb.like(cb.lower(root.get("email")), like))
                );
            }

            if (type != null) {
                predicate = cb.and(predicate, cb.equal(root.get("type"), type));
            }

            return predicate;
        };
    }
}