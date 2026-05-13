package com.epsel.epsel_api.modules.readings.servicesImpl;

import com.epsel.epsel_api.modules.readings.dto.CreateMeterReadingDTO;
import com.epsel.epsel_api.modules.readings.dto.MeterReadingResponseDTO;
import com.epsel.epsel_api.modules.readings.entities.MeterReading;
import com.epsel.epsel_api.modules.readings.enums.ReadingStatus;
import com.epsel.epsel_api.modules.readings.repositories.MeterReadingRepository;
import com.epsel.epsel_api.modules.readings.services.MeterReadingService;
import com.epsel.epsel_api.modules.readings.specifications.MeterReadingSpecification;
import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.modules.supplies.repositories.SupplyRepository;
import com.epsel.epsel_api.shared.exceptions.BadRequestException;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MeterReadingServiceImpl implements MeterReadingService {

    private final MeterReadingRepository repository;
    private final SupplyRepository supplyRepository;

    @Override
    public MeterReadingResponseDTO create(CreateMeterReadingDTO dto) {

        Supply supply = supplyRepository.findById(dto.getSupplyId())
                        .orElseThrow(() -> new ResourceNotFoundException("Suministro no encontrado"));

        Optional<MeterReading> lastReadingOpt = repository.findTopBySupplyOrderByReadingDateDesc(supply);

        int previousReading = lastReadingOpt
                .map(MeterReading::getCurrentReading)
                .orElse(0);

        if (dto.getCurrentReading() < previousReading) {
            throw new BadRequestException("La lectura actual no puede ser menor que la lectura anterior");
        }

        int consumption = dto.getCurrentReading() - previousReading;

        MeterReading reading = new MeterReading();

        reading.setSupply(supply);
        reading.setPreviousReading(previousReading);
        reading.setCurrentReading(dto.getCurrentReading());
        reading.setConsumption(consumption);
        reading.setReadingDate(dto.getReadingDate());
        reading.setStatus(ReadingStatus.RECORDED);
        reading.setObservations(dto.getObservations());

        MeterReading saved = repository.save(reading);
        return mapResponse(saved);
    }

    @Override
    public MeterReadingResponseDTO getById(UUID id) {
        MeterReading reading = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lectura no encontrada"));
        return mapResponse(reading);
    }

    @Override
    public Page<MeterReadingResponseDTO> search(UUID supplyId, ReadingStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return repository.
                findAll(MeterReadingSpecification.search(supplyId, status, startDate, endDate), pageable)
                .map(this::mapResponse);
    }

    private MeterReadingResponseDTO mapResponse(MeterReading reading) {

        return MeterReadingResponseDTO.builder()
                .id(reading.getId())
                .supplyId(reading.getSupply().getId())
                .supplyNumber(reading.getSupply().getSupplyNumber())
                .previousReading(reading.getPreviousReading())
                .currentReading(reading.getCurrentReading())
                .consumption(reading.getConsumption())
                .readingDate(reading.getReadingDate())
                .status(reading.getStatus())
                .observations(reading.getObservations())
                .build();
    }
}