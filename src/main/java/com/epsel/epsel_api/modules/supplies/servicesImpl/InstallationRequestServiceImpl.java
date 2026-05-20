package com.epsel.epsel_api.modules.supplies.servicesImpl;

import com.epsel.epsel_api.modules.configurations.entities.ServiceFeeConfiguration;
import com.epsel.epsel_api.modules.configurations.enums.ServiceFeeType;
import com.epsel.epsel_api.modules.configurations.repositories.ServiceFeeConfigurationRepository;
import com.epsel.epsel_api.modules.customers.entities.Customer;
import com.epsel.epsel_api.modules.customers.repositories.CustomerRepository;
import com.epsel.epsel_api.modules.properties.entities.Property;
import com.epsel.epsel_api.modules.properties.repositories.PropertyRepository;
import com.epsel.epsel_api.modules.supplies.dto.CreateInstallationRequestDTO;
import com.epsel.epsel_api.modules.supplies.dto.InstallationRequestResponseDTO;
import com.epsel.epsel_api.modules.supplies.entities.InstallationRequest;
import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.modules.supplies.enums.InstallationRequestStatus;
import com.epsel.epsel_api.modules.supplies.enums.SupplyStatus;
import com.epsel.epsel_api.modules.supplies.repositories.InstallationRequestRepository;
import com.epsel.epsel_api.modules.supplies.repositories.SupplyRepository;
import com.epsel.epsel_api.modules.supplies.services.InstallationRequestService;
import com.epsel.epsel_api.shared.exceptions.BadRequestException;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InstallationRequestServiceImpl implements InstallationRequestService {

    private final InstallationRequestRepository repository;
    private final CustomerRepository customerRepository;
    private final PropertyRepository propertyRepository;
    private final ServiceFeeConfigurationRepository feeRepository;
    private final SupplyRepository supplyRepository;

    @Override
    public InstallationRequestResponseDTO create(CreateInstallationRequestDTO dto) {

        Customer customer = customerRepository.findById(dto.getCustomerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        Property property = propertyRepository.findById(dto.getPropertyId())
                        .orElseThrow(() -> new ResourceNotFoundException("Propiedad no encontrada"));

        ServiceFeeConfiguration fee = feeRepository
                        .findByZone_IdAndFeeTypeAndActiveTrue(
                                property.getZone().getId(),
                                ServiceFeeType.INSTALLATION
                        )
                        .orElseThrow(() -> new ResourceNotFoundException("Instalación no disponible para la zona de la propiedad"));

        InstallationRequest request = new InstallationRequest();

        request.setCustomer(customer);
        request.setProperty(property);
        request.setInstallationCost(fee.getAmount());
        request.setStatus(InstallationRequestStatus.PENDING);
        request.setRequestedDate(dto.getRequestedDate());
        request.setObservations(dto.getObservations());

        InstallationRequest saved = repository.save(request);

        return InstallationRequestResponseDTO.builder()
                .id(saved.getId())
                .customerName(saved.getCustomer().getFullName())
                .propertyAddress(saved.getProperty().getAddress())
                .installationCost(saved.getInstallationCost())
                .status(saved.getStatus())
                .requestedDate(saved.getRequestedDate())
                .installationDate(saved.getInstallationDate())
                .observations(saved.getObservations())
                .build();
    }

    @Override
    public InstallationRequestResponseDTO approve(UUID id) {

        InstallationRequest request = repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Instalación no encontrada"));

        if (request.getStatus() != InstallationRequestStatus.PENDING) {
            throw new BadRequestException("Solo se pueden aprobar solicitudes pendientes");
        }

        request.setStatus(InstallationRequestStatus.APPROVED);

        InstallationRequest saved = repository.save(request);
        return mapResponse(saved);
    }

    @Override
    public InstallationRequestResponseDTO reject(UUID id, String observations) {

        InstallationRequest request = repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Instalación no encontrada"));

        if (request.getStatus() != InstallationRequestStatus.PENDING) {
            throw new BadRequestException("Solo se pueden rechazar solicitudes pendientes");
        }

        request.setStatus(InstallationRequestStatus.REJECTED);
        request.setObservations(observations);

        InstallationRequest saved = repository.save(request);
        return mapResponse(saved);
    }

    @Override
    public InstallationRequestResponseDTO install(UUID id) {

        InstallationRequest request = repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Instalación no encontrada"));

        if (request.getStatus() != InstallationRequestStatus.APPROVED) {
            throw new BadRequestException("Solo se pueden instalar solicitudes aprobadas");
        }

        Supply supply = new Supply();

        supply.setProperty(request.getProperty());
        supply.setStatus(SupplyStatus.ACTIVE);
        supply.setConnected(true);
        supply.setSupplyNumber(generateSupplyNumber());

        supplyRepository.save(supply);

        request.setStatus(InstallationRequestStatus.INSTALLED);
        request.setInstallationDate(LocalDate.now());

        InstallationRequest saved = repository.save(request);
        return mapResponse(saved);
    }

    private String generateSupplyNumber() {
        long count = supplyRepository.count() + 1;
        return String.format("SUM-%08d", count);
    }

    private InstallationRequestResponseDTO mapResponse(InstallationRequest request) {
        return InstallationRequestResponseDTO.builder()
                .id(request.getId())
                .customerName(request.getCustomer().getFullName())
                .propertyAddress(request.getProperty().getAddress())
                .installationCost(request.getInstallationCost())
                .status(request.getStatus())
                .requestedDate(request.getRequestedDate())
                .installationDate(request.getInstallationDate())
                .observations(request.getObservations())
                .build();
    }

}