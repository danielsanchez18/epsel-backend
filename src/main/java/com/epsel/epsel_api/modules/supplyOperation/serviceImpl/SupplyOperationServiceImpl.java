package com.epsel.epsel_api.modules.supplyOperation.serviceImpl;

import com.epsel.epsel_api.modules.supplyOperation.dto.response.SupplyOperationResponseDTO;
import com.epsel.epsel_api.modules.supplyOperation.entity.SupplyOperation;
import com.epsel.epsel_api.modules.supplyOperation.enums.SupplyOperationType;
import com.epsel.epsel_api.modules.supplyOperation.repository.SupplyOperationRepository;
import com.epsel.epsel_api.modules.supplyOperation.service.SupplyOperationService;
import com.epsel.epsel_api.modules.supplyOperation.specification.SupplyOperationSpecification;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplyOperationServiceImpl implements SupplyOperationService {

    private final SupplyOperationRepository repository;

    @Override
    public SupplyOperationResponseDTO getById(UUID id) {

        SupplyOperation operation = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Operación no encontrada"));

        if (operation.getDeleted()) {
            throw new ResourceNotFoundException("Operación no encontrada");
        }

        return mapResponse(operation);
    }

    @Override
    public Page<SupplyOperationResponseDTO> search(
            UUID supplyId,
            SupplyOperationType type,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {

        return repository.findAll(
                        SupplyOperationSpecification.search(
                                supplyId,
                                type,
                                startDate,
                                endDate
                        ),
                        pageable
                )
                .map(this::mapResponse);
    }

    private SupplyOperationResponseDTO mapResponse(
            SupplyOperation operation
    ) {

        return SupplyOperationResponseDTO.builder()
                .id(operation.getId())
                .supplyId(operation.getSupply().getId())
                .supplyNumber(operation.getSupply().getSupplyNumber())
                .operationType(operation.getOperationType())
                .operationDate(operation.getOperationDate())
                .reason(operation.getReason())
                .performedBy(operation.getPerformedBy())
                .observations(operation.getObservations())
                .build();
    }

}