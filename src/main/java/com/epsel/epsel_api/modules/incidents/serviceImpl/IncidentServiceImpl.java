package com.epsel.epsel_api.modules.incidents.serviceImpl;

import com.epsel.epsel_api.modules.customers.entities.Customer;
import com.epsel.epsel_api.modules.customers.repositories.CustomerRepository;
import com.epsel.epsel_api.modules.incidents.dto.response.IncidentResponseDTO;
import com.epsel.epsel_api.modules.incidents.dto.resquest.CreateIncidentDTO;
import com.epsel.epsel_api.modules.incidents.dto.resquest.ResolveIncidentDTO;
import com.epsel.epsel_api.modules.incidents.entity.Incident;
import com.epsel.epsel_api.modules.incidents.enums.IncidentPriority;
import com.epsel.epsel_api.modules.incidents.enums.IncidentStatus;
import com.epsel.epsel_api.modules.incidents.enums.IncidentType;
import com.epsel.epsel_api.modules.incidents.repository.IncidentRepository;
import com.epsel.epsel_api.modules.incidents.service.IncidentService;
import com.epsel.epsel_api.modules.incidents.specification.IncidentSpecification;
import com.epsel.epsel_api.modules.properties.entities.Property;
import com.epsel.epsel_api.modules.properties.repositories.PropertyRepository;
import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.modules.supplies.repositories.SupplyRepository;
import com.epsel.epsel_api.shared.exceptions.BadRequestException;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository repository;
    private final CustomerRepository customerRepository;
    private final PropertyRepository propertyRepository;
    private final SupplyRepository supplyRepository;

    @Override
    public IncidentResponseDTO create(CreateIncidentDTO dto) {

        Customer customer = null;
        Property property = null;
        Supply supply = null;

        if (dto.getCustomerId() != null) {
            customer = customerRepository.findByIdAndDeletedFalse(dto.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        }

        if (dto.getPropertyId() != null) {
            property = propertyRepository.findByIdAndDeletedFalse(dto.getPropertyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Predio no encontrado"));
        }

        if (dto.getSupplyId() != null) {
            supply = supplyRepository.findById(dto.getSupplyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Suministro no encontrado"));

            if (supply.getDeleted()) {
                throw new ResourceNotFoundException("Suministro no encontrado");
            }
        }

        Incident incident = new Incident();

        incident.setIncidentNumber(generateIncidentNumber());

        incident.setCustomer(customer);
        incident.setProperty(property);
        incident.setSupply(supply);

        incident.setType(dto.getType());
        incident.setPriority(dto.getPriority());

        incident.setStatus(IncidentStatus.OPEN);

        incident.setTitle(dto.getTitle());
        incident.setDescription(dto.getDescription());

        incident.setReportedDate(LocalDate.now());

        Incident saved = repository.save(incident);

        return mapResponse(saved);
    }

    @Override
    public IncidentResponseDTO getById(UUID id) {
        Incident incident = repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada"));

        if (incident.getDeleted()) {
            throw new ResourceNotFoundException("Incidencia no encontrada");
        }

        return mapResponse(incident);
    }

    @Override
    public Page<IncidentResponseDTO> search(
            IncidentStatus status,
            IncidentPriority priority,
            IncidentType type,
            UUID customerId,
            UUID supplyId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {

        return repository.findAll(
                        IncidentSpecification.search(
                                status,
                                priority,
                                type,
                                customerId,
                                supplyId,
                                startDate,
                                endDate
                        ),
                        pageable
                )
                .map(this::mapResponse);
    }

    @Override
    public IncidentResponseDTO startProgress(UUID id) {

        Incident incident = getIncident(id);

        if (incident.getStatus() != IncidentStatus.OPEN) {
            throw new BadRequestException("Solo incidencias abiertas pueden iniciar atención");
        }

        incident.setStatus(IncidentStatus.IN_PROGRESS);

        Incident saved = repository.save(incident);

        return mapResponse(saved);
    }

    @Override
    public IncidentResponseDTO resolve(UUID id, ResolveIncidentDTO dto) {

        Incident incident = getIncident(id);

        if (incident.getStatus() != IncidentStatus.IN_PROGRESS) {
            throw new BadRequestException("La incidencia debe estar en proceso");
        }

        incident.setStatus(IncidentStatus.RESOLVED);

        incident.setResolvedDate(LocalDate.now());

        incident.setResolution(dto.getResolution());

        Incident saved = repository.save(incident);

        return mapResponse(saved);
    }

    @Override
    public IncidentResponseDTO reject(UUID id, ResolveIncidentDTO dto) {

        Incident incident = getIncident(id);

        if (incident.getStatus() != IncidentStatus.OPEN && incident.getStatus() != IncidentStatus.IN_PROGRESS) {
            throw new BadRequestException("Solo incidencias abiertas o en proceso pueden ser rechazadas");
        }

        incident.setStatus(IncidentStatus.REJECTED);
        incident.setResolvedDate(LocalDate.now());
        incident.setResolution("RECHAZADO: " + dto.getResolution());

        Incident saved = repository.save(incident);

        return mapResponse(saved);
    }

    @Override
    public IncidentResponseDTO close(UUID id) {

        Incident incident = getIncident(id);

        if (incident.getStatus() != IncidentStatus.RESOLVED) {
            throw new BadRequestException("Solo incidencias resueltas pueden cerrarse");
        }

        incident.setStatus(IncidentStatus.CLOSED);

        Incident saved = repository.save(incident);

        return mapResponse(saved);
    }

    private Incident getIncident(UUID id) {
        Incident incident = repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Incidencia no encontrada"));
        if (incident.getDeleted()) {
            throw new ResourceNotFoundException("Incidencia no encontrada");
        }
        return incident;
    }

    private String generateIncidentNumber() {
        long count = repository.countByDeletedFalse() + 1;
        return String.format("INC-%06d", count);
    }

    private IncidentResponseDTO mapResponse(Incident incident) {

        return IncidentResponseDTO.builder()
                .id(incident.getId())
                .incidentNumber(incident.getIncidentNumber())
                .customerId(incident.getCustomer() != null ? incident.getCustomer().getId() : null)
                .customerName(incident.getCustomer() != null ? incident.getCustomer().getFullName() : null)
                .propertyId(incident.getProperty() != null ? incident.getProperty().getId() : null)
                .supplyId(incident.getSupply() != null ? incident.getSupply().getId() : null)
                .supplyNumber(incident.getSupply() != null ? incident.getSupply().getSupplyNumber() : null)
                .type(incident.getType())
                .priority(incident.getPriority())
                .status(incident.getStatus())
                .title(incident.getTitle())
                .description(incident.getDescription())
                .reportedDate(incident.getReportedDate())
                .resolvedDate(incident.getResolvedDate())
                .resolution(incident.getResolution())
                .build();
    }
}
