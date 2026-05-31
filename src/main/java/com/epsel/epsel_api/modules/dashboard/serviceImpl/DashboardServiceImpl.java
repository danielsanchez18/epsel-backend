package com.epsel.epsel_api.modules.dashboard.serviceImpl;

import com.epsel.epsel_api.modules.billing.enums.BillingStatus;
import com.epsel.epsel_api.modules.billing.projection.MonthlyBillingProjection;
import com.epsel.epsel_api.modules.billing.repositories.BillingRepository;
import com.epsel.epsel_api.modules.customers.repositories.CustomerRepository;
import com.epsel.epsel_api.modules.dashboard.dto.DashboardAlertDTO;
import com.epsel.epsel_api.modules.dashboard.dto.DashboardChartDTO;
import com.epsel.epsel_api.modules.dashboard.dto.DashboardKpiDTO;
import com.epsel.epsel_api.modules.dashboard.dto.KpiMetricDTO;
import com.epsel.epsel_api.modules.dashboard.dto.DashboardResponseDTO;
import com.epsel.epsel_api.modules.dashboard.service.DashboardService;
import com.epsel.epsel_api.modules.payments.projections.MonthlyPaymentProjection;
import com.epsel.epsel_api.modules.payments.repositories.PaymentRepository;
import com.epsel.epsel_api.modules.properties.repositories.PropertyRepository;
import com.epsel.epsel_api.modules.supplies.enums.SupplyStatus;
import com.epsel.epsel_api.modules.supplies.repositories.SupplyRepository;
import com.epsel.epsel_api.modules.supplyWorkOrder.repository.SupplyWorkOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final CustomerRepository customerRepository;
    private final PropertyRepository propertyRepository;
    private final SupplyRepository supplyRepository;
    private final BillingRepository billingRepository;
    private final PaymentRepository paymentRepository;
    private final SupplyWorkOrderRepository workOrderRepository;
    // private final IncidentRepository incidentRepository;

    @Override
    public DashboardResponseDTO getDashboard() {

        DashboardKpiDTO kpis = buildKpis();

        List<DashboardAlertDTO> alerts = buildAlerts();

        List<DashboardChartDTO> billingChart = buildBillingChart();

        List<DashboardChartDTO> paymentChart = buildPaymentChart();

        return DashboardResponseDTO.builder()
                .kpis(buildKpis())
                .alerts(buildAlerts())
                .billingChart(buildBillingChart())
                .paymentChart(buildPaymentChart())
                .consumptionChart(new ArrayList<>())
                .recentActivities(new ArrayList<>())
                .build();
    }

    private DashboardKpiDTO buildKpis() {
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        LocalDate lastMonthDate = now.minusMonths(1);
        int prevMonth = lastMonthDate.getMonthValue();
        int prevYear = lastMonthDate.getYear();

        // 1. Customers
        long totalCust = customerRepository.countByDeletedFalse();
        long newCust = customerRepository.countCreatedInMonth(month, year);
        KpiMetricDTO totalCustomers = KpiMetricDTO.builder()
                .value(totalCust)
                .change((double) newCust)
                .changeText(newCust > 0 ? "+" + newCust + " registrados este mes" : "Sin registros este mes")
                .build();

        // 2. Properties
        long totalProp = propertyRepository.countByDeletedFalse();
        long newProp = propertyRepository.countCreatedInMonth(month, year);
        KpiMetricDTO totalProperties = KpiMetricDTO.builder()
                .value(totalProp)
                .change((double) newProp)
                .changeText(newProp > 0 ? "+" + newProp + " registrados este mes" : "Sin registros este mes")
                .build();

        // 3. Active Supplies
        long totalAct = supplyRepository.countByStatusAndDeletedFalse(SupplyStatus.ACTIVE);
        long newAct = supplyRepository.countByStatusAndCreatedAtMonthAndYear(SupplyStatus.ACTIVE, month, year);
        KpiMetricDTO activeSupplies = KpiMetricDTO.builder()
                .value(totalAct)
                .change((double) newAct)
                .changeText(newAct > 0 ? "+" + newAct + " nuevos este mes" : "Sin nuevos este mes")
                .build();

        // 4. Suspended Supplies
        long totalSusp = supplyRepository.countByStatusAndDeletedFalse(SupplyStatus.SUSPENDED);
        long newSusp = supplyRepository.countByStatusAndCreatedAtMonthAndYear(SupplyStatus.SUSPENDED, month, year);
        KpiMetricDTO suspendedSupplies = KpiMetricDTO.builder()
                .value(totalSusp)
                .change(newSusp > 0 ? -((double) newSusp) : 0.0)
                .changeText(newSusp > 0 ? "+" + newSusp + " suspendidos este mes" : "Sin suspensiones este mes")
                .build();

        // 5. Cut Off Supplies
        long totalCut = supplyRepository.countByStatusAndDeletedFalse(SupplyStatus.CUT_OFF);
        long newCut = supplyRepository.countByStatusAndCreatedAtMonthAndYear(SupplyStatus.CUT_OFF, month, year);
        KpiMetricDTO cutOffSupplies = KpiMetricDTO.builder()
                .value(totalCut)
                .change(newCut > 0 ? -((double) newCut) : 0.0)
                .changeText(newCut > 0 ? "+" + newCut + " cortados este mes" : "Sin cortes este mes")
                .build();

        // 6. Pending Billings
        long totalPend = billingRepository.countByStatusAndDeletedFalse(BillingStatus.PENDING);
        long newPend = billingRepository.countByStatusAndCreatedAtMonthAndYear(BillingStatus.PENDING, month, year);
        KpiMetricDTO pendingBillings = KpiMetricDTO.builder()
                .value(totalPend)
                .change(newPend > 0 ? -((double) newPend) : 0.0)
                .changeText(newPend > 0 ? "+" + newPend + " pendientes este mes" : "Sin pendientes este mes")
                .build();

        // 7. Overdue Billings
        long totalOver = billingRepository.countByStatusAndDeletedFalse(BillingStatus.OVERDUE);
        long newOver = billingRepository.countByStatusAndCreatedAtMonthAndYear(BillingStatus.OVERDUE, month, year);
        KpiMetricDTO overdueBillings = KpiMetricDTO.builder()
                .value(totalOver)
                .change(newOver > 0 ? -((double) newOver) : 0.0)
                .changeText(newOver > 0 ? "+" + newOver + " vencidas este mes" : "Sin vencimientos este mes")
                .build();

        // 8. Total Billed Month
        java.math.BigDecimal billedThis = billingRepository.getTotalBilledMonth(month, year);
        java.math.BigDecimal billedPrev = billingRepository.getTotalBilledMonth(prevMonth, prevYear);
        double billedChange = 0.0;
        String billedChangeText = "Sin registros anteriores";
        if (billedPrev.compareTo(java.math.BigDecimal.ZERO) > 0) {
            double pct = billedThis.subtract(billedPrev)
                    .divide(billedPrev, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(new java.math.BigDecimal(100))
                    .doubleValue();
            billedChange = pct;
            billedChangeText = String.format("%s%.1f%% vs mes anterior", pct >= 0 ? "+" : "", pct);
        } else if (billedThis.compareTo(java.math.BigDecimal.ZERO) > 0) {
            billedChange = 100.0;
            billedChangeText = "+100% vs mes anterior";
        }
        KpiMetricDTO totalBilledMonth = KpiMetricDTO.builder()
                .value(billedThis)
                .change(billedChange)
                .changeText(billedChangeText)
                .build();

        // 9. Total Collected Month
        java.math.BigDecimal collectedThis = paymentRepository.getTotalCollectedMonth(month, year);
        java.math.BigDecimal collectedPrev = paymentRepository.getTotalCollectedMonth(prevMonth, prevYear);
        double collectedChange = 0.0;
        String collectedChangeText = "Sin registros anteriores";
        if (collectedPrev.compareTo(java.math.BigDecimal.ZERO) > 0) {
            double pct = collectedThis.subtract(collectedPrev)
                    .divide(collectedPrev, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(new java.math.BigDecimal(100))
                    .doubleValue();
            collectedChange = pct;
            collectedChangeText = String.format("%s%.1f%% vs mes anterior", pct >= 0 ? "+" : "", pct);
        } else if (collectedThis.compareTo(java.math.BigDecimal.ZERO) > 0) {
            collectedChange = 100.0;
            collectedChangeText = "+100% vs mes anterior";
        }
        KpiMetricDTO totalCollectedMonth = KpiMetricDTO.builder()
                .value(collectedThis)
                .change(collectedChange)
                .changeText(collectedChangeText)
                .build();

        // 10. Total Pending Collection
        java.math.BigDecimal pendingThis = billingRepository.getTotalPendingCollection();
        java.math.BigDecimal pendingThisMonthDebt = billingRepository.getTotalPendingBilledMonth(month, year);
        java.math.BigDecimal pendingPrevMonthDebt = billingRepository.getTotalPendingBilledMonth(prevMonth, prevYear);
        double pendingChange = 0.0;
        String pendingChangeText = "Sin deudas este mes";
        if (pendingPrevMonthDebt.compareTo(java.math.BigDecimal.ZERO) > 0) {
            double pct = pendingThisMonthDebt.subtract(pendingPrevMonthDebt)
                    .divide(pendingPrevMonthDebt, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(new java.math.BigDecimal(100))
                    .doubleValue();
            pendingChange = -pct;
            pendingChangeText = String.format("%s%.1f%% vs mes anterior", pct >= 0 ? "+" : "", pct);
        } else if (pendingThisMonthDebt.compareTo(java.math.BigDecimal.ZERO) > 0) {
            pendingChange = -100.0;
            pendingChangeText = "+100% vs mes anterior";
        }
        KpiMetricDTO totalPendingCollection = KpiMetricDTO.builder()
                .value(pendingThis)
                .change(pendingChange)
                .changeText(pendingChangeText)
                .build();

        return DashboardKpiDTO.builder()
                .totalCustomers(totalCustomers)
                .totalProperties(totalProperties)
                .activeSupplies(activeSupplies)
                .suspendedSupplies(suspendedSupplies)
                .cutOffSupplies(cutOffSupplies)
                .pendingBillings(pendingBillings)
                .overdueBillings(overdueBillings)
                .totalBilledMonth(totalBilledMonth)
                .totalCollectedMonth(totalCollectedMonth)
                .totalPendingCollection(totalPendingCollection)
                .build();
    }

    private List<DashboardAlertDTO> buildAlerts() {

        List<DashboardAlertDTO> alerts = new ArrayList<>();

        long overdueBillings = billingRepository.countByStatusAndDeletedFalse(BillingStatus.OVERDUE);

        if (overdueBillings > 0) {

            alerts.add(DashboardAlertDTO.builder()
                    .title("Facturas vencidas")
                    .description(
                            "Existen "
                                    + overdueBillings
                                    + " facturas vencidas pendientes de cobro")
                    .severity("HIGH")
                    .build());
        }

        long suspendedSupplies = supplyRepository.countByStatusAndDeletedFalse(SupplyStatus.SUSPENDED);

        if (suspendedSupplies > 0) {

            alerts.add(DashboardAlertDTO.builder()
                    .title("Suministros suspendidos")
                    .description(
                            "Actualmente existen "
                                    + suspendedSupplies
                                    + " suministros suspendidos"
                    )
                    .severity("MEDIUM")
                    .build());
        }

        long cutOffSupplies = supplyRepository.countByStatusAndDeletedFalse(SupplyStatus.CUT_OFF);

        if (cutOffSupplies > 0) {

            alerts.add(DashboardAlertDTO.builder()
                    .title("Suministros cortados")
                    .description(
                            "Actualmente existen "
                                    + cutOffSupplies
                                    + " suministros con corte de servicio"
                    )
                    .severity("HIGH")
                    .build());
        }

        return alerts;
    }

    private List<DashboardChartDTO> buildBillingChart() {

        Integer year = LocalDate.now().getYear();

        List<MonthlyBillingProjection> data = billingRepository.getBillingByMonth(year);

        return data.stream()
                .map(item ->
                        DashboardChartDTO.builder()
                                .label(getMonthName(item.getMonth()))
                                .value(item.getTotal())
                                .build()
                )
                .toList();
    }

    private List<DashboardChartDTO> buildPaymentChart() {

        Integer year = LocalDate.now().getYear();

        List<MonthlyPaymentProjection> data =
                paymentRepository.getPaymentsByMonth(year);

        return data.stream()
                .map(item ->
                        DashboardChartDTO.builder()
                                .label(getMonthName(item.getMonth()))
                                .value(item.getTotal())
                                .build()
                )
                .toList();
    }

    private String getMonthName(Integer month) {

        return switch (month) {

            case 1 -> "Ene";
            case 2 -> "Feb";
            case 3 -> "Mar";
            case 4 -> "Abr";
            case 5 -> "May";
            case 6 -> "Jun";
            case 7 -> "Jul";
            case 8 -> "Ago";
            case 9 -> "Sep";
            case 10 -> "Oct";
            case 11 -> "Nov";
            case 12 -> "Dic";

            default -> "";
        };
    }
}