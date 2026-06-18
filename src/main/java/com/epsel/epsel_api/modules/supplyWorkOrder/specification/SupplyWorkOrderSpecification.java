package com.epsel.epsel_api.modules.supplyWorkOrder.specification;

import com.epsel.epsel_api.modules.supplyWorkOrder.entity.SupplyWorkOrder;
import com.epsel.epsel_api.modules.supplyWorkOrder.enums.WorkOrderStatus;
import com.epsel.epsel_api.modules.supplyWorkOrder.enums.WorkOrderType;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class SupplyWorkOrderSpecification {

    public static Specification<SupplyWorkOrder> search(
            UUID supplyId,
            WorkOrderType type,
            WorkOrderStatus status,
            java.time.LocalDateTime startDate,
            java.time.LocalDateTime endDate
    ) {

        return (root, query, cb) -> {

            var predicate = cb.conjunction();

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
                                root.get("type"),
                                type
                        )
                );
            }

            if (status != null) {
                predicate = cb.and(
                        predicate,
                        cb.equal(
                                root.get("status"),
                                status
                        )
                );
            }

            if (startDate != null) {
                predicate = cb.and(
                        predicate,
                        cb.greaterThanOrEqualTo(
                                root.get("createdAt"),
                                startDate
                        )
                );
            }

            if (endDate != null) {
                predicate = cb.and(
                        predicate,
                        cb.lessThanOrEqualTo(
                                root.get("createdAt"),
                                endDate
                        )
                );
            }

            return predicate;
        };
    }
}