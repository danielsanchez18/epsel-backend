package com.epsel.epsel_api.modules.payments.servicesImpl;

import com.epsel.epsel_api.modules.billing.entities.Billing;
import com.epsel.epsel_api.modules.billing.enums.BillingStatus;
import com.epsel.epsel_api.modules.billing.repositories.BillingRepository;
import com.epsel.epsel_api.modules.payments.dto.CancelPaymentDTO;
import com.epsel.epsel_api.modules.payments.dto.CreatePaymentDTO;
import com.epsel.epsel_api.modules.payments.dto.PaymentResponseDTO;
import com.epsel.epsel_api.modules.payments.entities.Payment;
import com.epsel.epsel_api.modules.payments.enums.PaymentMethod;
import com.epsel.epsel_api.modules.payments.enums.PaymentStatus;
import com.epsel.epsel_api.modules.payments.repositories.PaymentRepository;
import com.epsel.epsel_api.modules.payments.services.PaymentService;
import com.epsel.epsel_api.modules.payments.specifications.PaymentSpecification;
import com.epsel.epsel_api.modules.users.repositories.UserRepository;
import com.epsel.epsel_api.shared.exceptions.BadRequestException;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final BillingRepository billingRepository;
    private final UserRepository userRepository;

    @Override
    public PaymentResponseDTO create(CreatePaymentDTO dto) {

        Billing billing = billingRepository.findById(dto.getBillingId())
                .orElseThrow(() -> new ResourceNotFoundException("Factura no encontrada"));

        if (billing.getDeleted()) {
            throw new ResourceNotFoundException("Factura no encontrada");
        }

        if (billing.getStatus() == BillingStatus.PAID) {
            throw new BadRequestException("La factura ya está pagada");
        }

        if (billing.getStatus() == BillingStatus.CANCELLED) {
            throw new BadRequestException("La factura está anulada");
        }

        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El monto debe ser mayor a cero");
        }

        if (dto.getAmount().compareTo(billing.getPendingAmount()) > 0) {
            throw new BadRequestException(
                    "El monto excede el saldo pendiente: " + billing.getPendingAmount()
            );
        }

        if (dto.getPaymentMethod() != PaymentMethod.CASH
                && (dto.getOperationNumber() == null
                || dto.getOperationNumber().isBlank())) {

            throw new BadRequestException("Número de operación requerido");
        }

        if (dto.getOperationNumber() != null
                && repository.existsByOperationNumber(dto.getOperationNumber())) {
            throw new BadRequestException("Número de operación ya registrado");
        }

        // Buscar el usuario que registra el pago
        var registeredByUser = userRepository.findById(dto.getRegisteredBy())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Payment payment = new Payment();

        payment.setBilling(billing);
        payment.setReceiptNumber(generateReceiptNumber());
        payment.setAmount(dto.getAmount());
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setOperationNumber(dto.getOperationNumber());
        payment.setObservations(dto.getObservations());
        payment.setRegisteredBy(registeredByUser);

        Payment saved = repository.save(payment);

        BigDecimal newPaidAmount =
                billing.getAmountPaid().add(dto.getAmount());

        BigDecimal pending =
                billing.getTotalAmount().subtract(newPaidAmount);

        billing.setAmountPaid(newPaidAmount);
        billing.setPendingAmount(pending);

        if (pending.compareTo(BigDecimal.ZERO) == 0) {

            billing.setStatus(BillingStatus.PAID);
            billing.setPaidDate(LocalDate.now());

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
    public Page<PaymentResponseDTO> search(String receiptNumber, String billingNumber, String supplyNumber, String customerName, PaymentMethod paymentMethod, PaymentStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return repository.findAll(
                PaymentSpecification.search(
                        receiptNumber,
                        billingNumber,
                        supplyNumber,
                        customerName,
                        paymentMethod,
                        status,
                        startDate,
                        endDate
                ),
                pageable
        ).map(this::mapResponse);
    }

    @Override
    public Page<PaymentResponseDTO> getByBilling(UUID billingId, Pageable pageable) {
        return repository
                .findByBillingIdAndDeletedFalse(billingId, pageable)
                .map(this::mapResponse);
    }

    @Override
    public Page<PaymentResponseDTO> getByCustomer(UUID customerId, Pageable pageable) {
        return repository
                .findByCustomerIdAndDeletedFalse(customerId, pageable)
                .map(this::mapResponse);
    }

    @Override
    public com.epsel.epsel_api.modules.payments.dto.PaymentKpiDTO getKpis(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal totalToday = repository.sumCompletedPaymentsToday();
        BigDecimal totalPeriod = repository.sumCompletedPaymentsBetweenDates(startDate, endDate);
        BigDecimal totalCash = repository.sumCompletedPaymentsByMethodBetweenDates(PaymentMethod.CASH, startDate, endDate);
        BigDecimal totalYape = repository.sumCompletedPaymentsByMethodBetweenDates(PaymentMethod.YAPE, startDate, endDate);
        BigDecimal totalTransfer = repository.sumCompletedPaymentsByMethodsBetweenDates(
                java.util.List.of(PaymentMethod.BANK_TRANSFER, PaymentMethod.PLIN, PaymentMethod.CARD),
                startDate,
                endDate
        );

        return com.epsel.epsel_api.modules.payments.dto.PaymentKpiDTO.builder()
                .totalToday(totalToday)
                .totalPeriod(totalPeriod)
                .totalCash(totalCash)
                .totalYape(totalYape)
                .totalTransfer(totalTransfer)
                .build();
    }


    @Override
    public PaymentResponseDTO cancel(
            UUID paymentId,
            CancelPaymentDTO dto
    ) {

        Payment payment = repository.findById(paymentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pago no encontrado"));

        if (payment.getStatus() == PaymentStatus.CANCELLED) {
            throw new BadRequestException("El pago ya está cancelado");
        }

        Billing billing = payment.getBilling();

        BigDecimal newPaidAmount =
                billing.getAmountPaid()
                        .subtract(payment.getAmount());

        BigDecimal newPendingAmount =
                billing.getTotalAmount()
                        .subtract(newPaidAmount);

        billing.setAmountPaid(newPaidAmount);
        billing.setPendingAmount(newPendingAmount);

        if (newPaidAmount.compareTo(BigDecimal.ZERO) == 0) {

            billing.setStatus(BillingStatus.PENDING);

        } else {

            billing.setStatus(BillingStatus.PARTIALLY_PAID);

        }

        billingRepository.save(billing);

        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setCancellationReason(dto.getReason());
        payment.setCancelledAt(LocalDateTime.now());

        Payment saved = repository.save(payment);

        return mapResponse(saved);
    }

    private PaymentResponseDTO mapResponse(Payment payment) {

        Billing billing = payment.getBilling();

        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .receiptNumber(payment.getReceiptNumber())

                .billingId(billing.getId())
                .billingNumber(billing.getBillingNumber())

                .supplyId(billing.getSupply().getId())
                .supplyNumber(billing.getSupply().getSupplyNumber())

                .customerFullName(billing.getSupply()
                                .getCustomer()
                                .getFullName())

                .amount(payment.getAmount())

                .billingTotalAmount(billing.getTotalAmount())
                .billingPendingAmount(billing.getPendingAmount())

                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .paymentDate(payment.getPaymentDate())

                .operationNumber(payment.getOperationNumber())
                .observations(payment.getObservations())

                .registeredById(payment.getRegisteredBy() != null ? payment.getRegisteredBy().getId() : null)
                .registeredBy(payment.getRegisteredBy() != null
                        ? payment.getRegisteredBy().getNames() + " " + payment.getRegisteredBy().getLastNames()
                        : "Sistema")
                .build();
    }

    private String generateReceiptNumber() {
        long count = repository.count() + 1;
        return String.format("REC-%08d", count);
    }
}