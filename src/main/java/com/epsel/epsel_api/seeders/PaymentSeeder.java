package com.epsel.epsel_api.seeders;

import com.epsel.epsel_api.modules.billing.entities.Billing;
import com.epsel.epsel_api.modules.billing.repositories.BillingRepository;
import com.epsel.epsel_api.modules.payments.entities.Payment;
import com.epsel.epsel_api.modules.payments.enums.PaymentMethod;
import com.epsel.epsel_api.modules.payments.enums.PaymentStatus;
import com.epsel.epsel_api.modules.payments.repositories.PaymentRepository;
import com.epsel.epsel_api.modules.users.entities.User;
import com.epsel.epsel_api.modules.users.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentSeeder {

    private final PaymentRepository paymentRepository;
    private final BillingRepository billingRepository;
    private final UserRepository userRepository;

    private final Random random = new Random();

    public void generate() {

        if (paymentRepository.count() > 0) {
            return;
        }

        User admin =
                userRepository.findAll()
                        .stream()
                        .findFirst()
                        .orElse(null);

        List<Billing> billings =
                billingRepository.findAll();

        int counter = 1;

        for (Billing billing : billings) {

            switch (billing.getStatus()) {

                case PAID -> {

                    Payment payment = new Payment();

                    payment.setBilling(billing);

                    payment.setReceiptNumber(
                            String.format(
                                    "REC-%08d",
                                    counter++
                            )
                    );

                    payment.setAmount(
                            billing.getTotalAmount()
                    );

                    payment.setPaymentMethod(
                            randomMethod()
                    );

                    payment.setStatus(
                            PaymentStatus.COMPLETED
                    );

                    payment.setPaymentDate(
                            billing.getBillingDate()
                                    .plusDays(
                                            random.nextInt(20) + 1
                                    )
                                    .atTime(
                                            10 + random.nextInt(8),
                                            random.nextInt(60)
                                    )
                    );

                    payment.setOperationNumber(
                            "OP-" + UUID.randomUUID()
                                    .toString()
                                    .substring(0, 8)
                    );

                    payment.setRegisteredBy(admin);

                    paymentRepository.save(payment);
                }

                case PARTIALLY_PAID -> {

                    BigDecimal amount =
                            billing.getTotalAmount()
                                    .multiply(
                                            BigDecimal.valueOf(
                                                    0.4 +
                                                            random.nextDouble() * 0.4
                                            )
                                    )
                                    .setScale(
                                            2,
                                            RoundingMode.HALF_UP
                                    );

                    Payment payment = new Payment();

                    payment.setBilling(billing);

                    payment.setReceiptNumber(
                            String.format(
                                    "REC-%08d",
                                    counter++
                            )
                    );

                    payment.setAmount(amount);

                    payment.setPaymentMethod(
                            randomMethod()
                    );

                    payment.setStatus(
                            PaymentStatus.COMPLETED
                    );

                    payment.setPaymentDate(
                            billing.getBillingDate()
                                    .plusDays(
                                            random.nextInt(15) + 1
                                    )
                                    .atTime(
                                            9 + random.nextInt(10),
                                            random.nextInt(60)
                                    )
                    );

                    payment.setOperationNumber(
                            "OP-" + UUID.randomUUID()
                                    .toString()
                                    .substring(0, 8)
                    );

                    payment.setRegisteredBy(admin);

                    paymentRepository.save(payment);
                }

                default -> {
                    // No generar pagos
                }
            }
        }

        System.out.println(
                "Payments generados: "
                        + paymentRepository.count()
        );
    }

    private PaymentMethod randomMethod() {

        PaymentMethod[] methods =
                PaymentMethod.values();

        return methods[
                random.nextInt(
                        methods.length
                )
                ];
    }
}