package com.epsel.epsel_api.modules.supplyWorkOrder.repository;

import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.modules.supplyWorkOrder.entity.SupplyWorkOrder;
import com.epsel.epsel_api.modules.supplyWorkOrder.enums.WorkOrderStatus;
import com.epsel.epsel_api.modules.supplyWorkOrder.enums.WorkOrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface SupplyWorkOrderRepository extends
        JpaRepository<SupplyWorkOrder, UUID>,
        JpaSpecificationExecutor<SupplyWorkOrder> {

    boolean existsBySupplyAndTypeAndStatusIn(
            Supply supply,
            WorkOrderType type,
            List<WorkOrderStatus> statuses
    );

}
