package com.epsel.epsel_api.modules.supplyOperation.repository;

import com.epsel.epsel_api.modules.supplyOperation.entity.SupplyOperation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface SupplyOperationRepository extends
        JpaRepository<SupplyOperation, UUID>,
        JpaSpecificationExecutor<SupplyOperation> {



}