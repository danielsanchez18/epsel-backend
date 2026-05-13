package com.epsel.epsel_api.modules.payments.servicesImpl;

import com.epsel.epsel_api.modules.billing.entities.Billing;
import com.epsel.epsel_api.modules.billing.enums.BillingStatus;
import com.epsel.epsel_api.modules.billing.repositories.BillingRepository;
import com.epsel.epsel_api.modules.payments.dto.CreatePaymentDTO;
import com.epsel.epsel_api.modules.payments.dto.PaymentResponseDTO;
import com.epsel.epsel_api.modules.payments.entities.Payment;
import com.epsel.epsel_api.modules.payments.enums.PaymentStatus;
import com.epsel.epsel_api.modules.payments.repositories.PaymentRepository;
import com.epsel.epsel_api.modules.payments.services.PaymentService;
import com.epsel.epsel_api.shared.exceptions.BadRequestException;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final BillingRepository billingRepository;

    @Override
    public PaymentResponseDTO create(CreatePaymentDTO dto) {

        Billing billing = billingRepository.findById(dto.getBillingId())
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada"));

        if (billing.getStatus() == BillingStatus.PAID) {
            throw new BadRequestException("La factura ya está pagada");
        }

        BigDecimal paidAmount = repository
                .sumByBillingIdAndStatus(billing.getId(), PaymentStatus.COMPLETED);

        if (paidAmount == null) {
            paidAmount = BigDecimal.ZERO;
        }

        BigDecimal remaining = billing.getTotalAmount().subtract(paidAmount);

        if (dto.getAmount().compareTo(remaining) > 0) {
            throw new BadRequestException("El monto del pago excede el monto restante de la factura: " + remaining);
        }

        Payment payment = new Payment();

        payment.setBilling(billing);
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setOperationNumber(dto.getOperationNumber());
        payment.setObservations(dto.getObservations());

        Payment saved = repository.save(payment);

        BigDecimal newPaidAmount = paidAmount.add(dto.getAmount());

        if (newPaidAmount.compareTo(billing.getTotalAmount()) == 0) {
            billing.setStatus(BillingStatus.PAID);
        } else {
            billing.setStatus(BillingStatus.PARTIALLY_PAID);
        }

        billingRepository.save(billing);
        return mapResponse(saved);
    }

    @Override
    public PaymentResponseDTO getById(UUID id) {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));
        return mapResponse(payment);
    }

    @Override
    public Page<PaymentResponseDTO> getByBilling(UUID billingId, Pageable pageable) {
        return repository
                .findByBillingIdAndDeletedFalse(billingId, pageable)
                .map(this::mapResponse);
    }

    private PaymentResponseDTO mapResponse(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .billingId(payment.getBilling().getId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .paymentDate(payment.getPaymentDate())
                .operationNumber(payment.getOperationNumber())
                .observations(payment.getObservations())
                .build();
    }
}