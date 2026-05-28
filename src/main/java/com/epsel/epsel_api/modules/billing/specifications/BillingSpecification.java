package com.epsel.epsel_api.modules.billing.specifications;

import com.epsel.epsel_api.modules.billing.entities.Billing;
import com.epsel.epsel_api.modules.billing.enums.BillingStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class BillingSpecification {

    public static Specification<Billing> search(
            String billingNumber,
            String customerName,
            BillingStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Boolean overdue
    ) {

        return (root, query, cb) -> {

            Predicate predicate = cb.conjunction();

            predicate = cb.and(
                    predicate,
                    cb.isFalse(root.get("deleted"))
            );

            if (billingNumber != null && !billingNumber.isBlank()) {

                predicate = cb.and(
                        predicate,
                        cb.like(
                                cb.lower(root.get("billingNumber")),
                                "%" + billingNumber.toLowerCase() + "%"
                        )
                );
            }

            if (customerName != null && !customerName.isBlank()) {

                predicate = cb.and(
                        predicate,
                        cb.like(
                                cb.lower(
                                        root.get("supply")
                                                .get("customer")
                                                .get("fullName")
                                ),
                                "%" + customerName.toLowerCase() + "%"
                        )
                );
            }

            if (status != null) {

                predicate = cb.and(
                        predicate,
                        cb.equal(root.get("status"), status)
                );
            }

            if (startDate != null) {

                predicate = cb.and(
                        predicate,
                        cb.greaterThanOrEqualTo(
                                root.get("billingDate"),
                                startDate
                        )
                );
            }

            if (endDate != null) {

                predicate = cb.and(
                        predicate,
                        cb.lessThanOrEqualTo(
                                root.get("billingDate"),
                                endDate
                        )
                );
            }

            if (Boolean.TRUE.equals(overdue)) {

                predicate = cb.and(
                        predicate,
                        cb.lessThan(
                                root.get("dueDate"),
                                LocalDate.now()
                        )
                );

                predicate = cb.and(
                        predicate,
                        cb.notEqual(
                                root.get("status"),
                                BillingStatus.PAID
                        )
                );
            }

            query.orderBy(
                    cb.desc(root.get("createdAt"))
            );

            return predicate;
        };
    }

}
