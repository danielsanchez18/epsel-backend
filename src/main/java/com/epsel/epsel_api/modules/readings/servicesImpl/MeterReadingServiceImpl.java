package com.epsel.epsel_api.modules.readings.servicesImpl;

import com.epsel.epsel_api.modules.readings.dto.CreateMeterReadingDTO;
import com.epsel.epsel_api.modules.readings.dto.MeterReadingResponseDTO;
import com.epsel.epsel_api.modules.readings.dto.ReadingKpisDTO;
import com.epsel.epsel_api.modules.readings.entities.MeterReading;
import com.epsel.epsel_api.modules.readings.enums.ReadingStatus;
import com.epsel.epsel_api.modules.readings.repositories.MeterReadingRepository;
import com.epsel.epsel_api.modules.readings.services.MeterReadingService;
import com.epsel.epsel_api.modules.readings.specifications.MeterReadingSpecification;
import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.modules.supplies.enums.SupplyStatus;
import com.epsel.epsel_api.modules.supplies.repositories.SupplyRepository;
import com.epsel.epsel_api.shared.exceptions.BadRequestException;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import com.epsel.epsel_api.shared.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MeterReadingServiceImpl implements MeterReadingService {

    private final MeterReadingRepository repository;
    private final SupplyRepository supplyRepository;
    private final StorageService storageService;

    @Override
    public MeterReadingResponseDTO create(CreateMeterReadingDTO dto, MultipartFile meterPhoto) {

        Supply supply = supplyRepository.findById(dto.getSupplyId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Suministro no encontrado"));

        if (supply.getDeleted()) {
            throw new ResourceNotFoundException("Suministro no encontrado");
        }

        if (supply.getStatus() != SupplyStatus.ACTIVE) {
            throw new BadRequestException(
                    "Solo se pueden registrar lecturas para suministros activos"
            );
        }

        LocalDate startOfMonth = dto.getReadingDate().withDayOfMonth(1);
        LocalDate endOfMonth = dto.getReadingDate().withDayOfMonth(dto.getReadingDate().lengthOfMonth());

        Boolean exists = repository.existsBySupplyAndReadingDateBetweenAndStatusIn(
                        supply,
                        startOfMonth,
                        endOfMonth,
                        List.of(
                                ReadingStatus.RECORDED,
                                ReadingStatus.VALIDATED,
                                ReadingStatus.BILLED
                        ));

        if (Boolean.TRUE.equals(exists)) {
            throw new BadRequestException(
                    "Ya existe una lectura registrada para este periodo"
            );
        }

        Integer previousReading = supply.getLastReading() != null
                ? supply.getLastReading()
                : 0;

        if (dto.getCurrentReading() < previousReading) {
            throw new BadRequestException(
                    "La lectura actual no puede ser menor a la anterior"
            );
        }

        Integer consumption =
                dto.getCurrentReading() - previousReading;

        MeterReading reading = new MeterReading();

        reading.setSupply(supply);
        reading.setPreviousReading(previousReading);
        reading.setCurrentReading(dto.getCurrentReading());
        reading.setConsumption(consumption);
        reading.setReadingDate(dto.getReadingDate());
        reading.setStatus(ReadingStatus.RECORDED);
        
        if (meterPhoto != null && !meterPhoto.isEmpty()) {
            String meterPhotoUrl = storageService.upload(meterPhoto);
            reading.setMeterPhotoUrl(meterPhotoUrl);
        }
        
        reading.setOcrValue(dto.getOcrValue());
        reading.setObservations(dto.getObservations());

        MeterReading saved = repository.save(reading);

        supply.setLastReading(dto.getCurrentReading());

        supplyRepository.save(supply);

        return mapResponse(saved);
    }

    @Override
    public MeterReadingResponseDTO getById(UUID id) {
        MeterReading reading = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Lectura no encontrada"));
        return mapResponse(reading);
    }

    @Override
    public Page<MeterReadingResponseDTO> search(String search, UUID zoneId, ReadingStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return repository.
                findAll(MeterReadingSpecification.search(search, zoneId, status, startDate, endDate), pageable)
                .map(this::mapResponse);
    }

    @Override
    public MeterReadingResponseDTO validate(UUID id) {
        MeterReading reading = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lectura no encontrada"));

        if (reading.getDeleted()) {
            throw new ResourceNotFoundException("Lectura no encontrada");
        }

        if (reading.getStatus() != ReadingStatus.RECORDED) {
            throw new BadRequestException("Solo se pueden validar lecturas registradas");
        }

        reading.setStatus(ReadingStatus.VALIDATED);

        MeterReading saved = repository.save(reading);

        return mapResponse(saved);
    }

    @Override
    public MeterReadingResponseDTO cancel(UUID id, String observations) {

        MeterReading reading = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lectura no encontrada"));

        if (reading.getDeleted()) {
            throw new ResourceNotFoundException("Lectura no encontrada");
        }

        if (reading.getStatus() == ReadingStatus.BILLED) {
            throw new BadRequestException("No se puede cancelar una lectura facturada");
        }

        reading.setStatus(ReadingStatus.CANCELLED);
        reading.setObservations(observations);

        MeterReading saved = repository.save(reading);

        Supply supply = reading.getSupply();

        Optional<MeterReading> lastValidReading =
                repository.findTopBySupplyAndStatusInAndIdNotOrderByReadingDateDesc(
                        supply,
                        List.of(
                                ReadingStatus.RECORDED,
                                ReadingStatus.VALIDATED,
                                ReadingStatus.BILLED
                        ),
                        reading.getId()
                );

        Integer lastReading = lastValidReading
                .map(MeterReading::getCurrentReading)
                .orElse(0);

        supply.setLastReading(lastReading);

        supplyRepository.save(supply);

        return mapResponse(saved);
    }

    private MeterReadingResponseDTO mapResponse(MeterReading reading) {

        return MeterReadingResponseDTO.builder()
                .id(reading.getId().toString())
                .supplyId(reading.getSupply().getId().toString())
                .supplyNumber(reading.getSupply().getSupplyNumber())
                .customerName(reading.getSupply().getCustomer().getFullName())
                .meterNumber(reading.getSupply().getMeterNumber())
                .previousReading(reading.getPreviousReading())
                .currentReading(reading.getCurrentReading())
                .consumption(reading.getConsumption())
                .readingDate(reading.getReadingDate().toString())
                .status(reading.getStatus())
                .meterPhotoUrl(reading.getMeterPhotoUrl())
                .ocrValue(reading.getOcrValue())
                .observations(reading.getObservations())
                .build();
    }

    @Override
    public ReadingKpisDTO getKpis() {
        java.time.LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        long registeredToday = repository.countByDeletedFalseAndCreatedAtAfter(startOfToday);

        long pending = repository.countByStatusAndDeletedFalse(ReadingStatus.RECORDED);
        long validated = repository.countByStatusAndDeletedFalse(ReadingStatus.VALIDATED);
        long billed = repository.countByStatusAndDeletedFalse(ReadingStatus.BILLED);
        long cancelled = repository.countByStatusAndDeletedFalse(ReadingStatus.CANCELLED);

        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        long monthConsumption = repository.sumConsumptionByReadingDateAfterAndDeletedFalse(startOfMonth);

        return ReadingKpisDTO.builder()
                .registeredToday(registeredToday)
                .pending(pending)
                .validated(validated)
                .billed(billed)
                .cancelled(cancelled)
                .monthConsumption(monthConsumption)
                .build();
    }
}