package com.epsel.epsel_api.modules.supplyOperation.dto.request;

import com.epsel.epsel_api.modules.supplyOperation.enums.SupplyOperationType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreateSupplyOperationDTO {

    @NotNull(message = "El suministro es requerido")
    private UUID supplyId;

    @NotNull(message = "El tipo de operación es requerido")
    private SupplyOperationType operationType;

    @NotNull(message = "La fecha de operación es requerida")
    private LocalDate operationDate;

    private String reason;

    private String observations;

}