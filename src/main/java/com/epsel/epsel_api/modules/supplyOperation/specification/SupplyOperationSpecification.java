package com.epsel.epsel_api.modules.supplyOperation.specification;

import com.epsel.epsel_api.modules.supplyOperation.entity.SupplyOperation;
import com.epsel.epsel_api.modules.supplyOperation.enums.SupplyOperationType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public class SupplyOperationSpecification {

    public static Specification<SupplyOperation> search(
            UUID supplyId,
            SupplyOperationType type,
            LocalDate startDate,
            LocalDate endDate
    ) {

        return (root, query, cb) -> {

            Predicate predicate = cb.conjunction();

            predicate = cb.and(
                    predicate,
                    cb.isFalse(root.get("deleted"))
            );

            if (supplyId != null) {

                predicate = cb.and(
                        predicate,
                        cb.equal(
                                root.get("supply").get("id"),
                                supplyId
                        )
                );
            }

            if (type != null) {

                predicate = cb.and(
                        predicate,
                        cb.equal(
                                root.get("operationType"),
                                type
                        )
                );
            }

            if (startDate != null) {

                predicate = cb.and(
                        predicate,
                        cb.greaterThanOrEqualTo(
                                root.get("operationDate"),
                                startDate
                        )
                );
            }

            if (endDate != null) {

                predicate = cb.and(
                        predicate,
                        cb.lessThanOrEqualTo(
                                root.get("operationDate"),
                                endDate
                        )
                );
            }

            return predicate;
        };
    }
}
