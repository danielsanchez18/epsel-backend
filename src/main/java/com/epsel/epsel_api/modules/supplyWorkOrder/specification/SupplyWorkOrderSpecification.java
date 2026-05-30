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
            WorkOrderStatus status
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

            return predicate;
        };
    }
}