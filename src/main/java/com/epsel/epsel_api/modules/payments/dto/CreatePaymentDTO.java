package com.epsel.epsel_api.modules.payments.dto;

import com.epsel.epsel_api.modules.payments.enums.PaymentMethod;
import com.epsel.epsel_api.modules.users.entities.User;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CreatePaymentDTO {

    @NotNull(message = "La factura es requerida")
    private UUID billingId;

    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @NotNull(message = "El método de pago es requerido")
    private PaymentMethod paymentMethod;

    private String operationNumber;
    private String observations;

    @NotNull(message = "El usuario que registra el pago es requerido")
    private UUID registeredBy;

}