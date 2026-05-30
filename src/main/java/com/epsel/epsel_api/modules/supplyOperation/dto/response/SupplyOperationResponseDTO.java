package com.epsel.epsel_api.modules.supplyOperation.dto.response;

import com.epsel.epsel_api.modules.supplyOperation.enums.SupplyOperationType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
public class SupplyOperationResponseDTO {

    private UUID id;
    private UUID supplyId;
    private String supplyNumber;
    private SupplyOperationType operationType;
    private LocalDate operationDate;
    private String reason;
    private String performedBy;
    private String observations;

}
