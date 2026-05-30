package com.epsel.epsel_api.modules.payments.specifications;

import com.epsel.epsel_api.modules.payments.entities.Payment;
import com.epsel.epsel_api.modules.payments.enums.PaymentMethod;
import com.epsel.epsel_api.modules.payments.enums.PaymentStatus;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PaymentSpecification {

    public static Specification<Payment> search(
            String receiptNumber,
            String billingNumber,
            String supplyNumber,
            String customerName,
            PaymentMethod paymentMethod,
            PaymentStatus status,
            LocalDate startDate,
            LocalDate endDate
    ) {

        return (root, query, cb) -> {

            Predicate predicate = cb.conjunction();

            predicate = cb.and(predicate, cb.isFalse(root.get("deleted")));

            if (receiptNumber != null && !receiptNumber.isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("receiptNumber")), "%" + receiptNumber.toLowerCase() + "%"));
            }

            if (billingNumber != null && !billingNumber.isBlank()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("billing").get("billingNumber")), "%" + billingNumber.toLowerCase() + "%"));
            }

            if (supplyNumber != null && !supplyNumber.isBlank()) {

                predicate = cb.and(
                        predicate,
                        cb.like(
                                cb.lower(
                                        root.get("billing")
                                                .get("supply")
                                                .get("supplyNumber")
                                ),
                                "%" + supplyNumber.toLowerCase() + "%"
                        )
                );
            }

            if (customerName != null && !customerName.isBlank()) {

                Expression<String> fullName = cb.concat(
                        cb.concat(
                                root.get("billing")
                                        .get("supply")
                                        .get("customer")
                                        .get("firstName"),
                                " "
                        ),
                        root.get("billing")
                                .get("supply")
                                .get("customer")
                                .get("lastName")
                );

                predicate = cb.and(
                        predicate,
                        cb.like(
                                cb.lower(fullName),
                                "%" + customerName.toLowerCase() + "%"
                        )
                );
            }

            if (paymentMethod != null) {

                predicate = cb.and(
                        predicate,
                        cb.equal(root.get("paymentMethod"), paymentMethod)
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
                                root.get("paymentDate"),
                                startDate.atStartOfDay()
                        )
                );
            }

            if (endDate != null) {

                predicate = cb.and(
                        predicate,
                        cb.lessThanOrEqualTo(
                                root.get("paymentDate"),
                                endDate.atTime(23, 59, 59)
                        )
                );
            }

            return predicate;
        };
    }
}