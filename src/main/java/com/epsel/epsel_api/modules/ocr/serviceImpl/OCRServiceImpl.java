package com.epsel.epsel_api.modules.ocr.serviceImpl;

import com.epsel.epsel_api.config.OCRProperties;
import com.epsel.epsel_api.modules.ocr.dto.OCRApiResponse;
import com.epsel.epsel_api.modules.ocr.dto.OCRScanResponseDTO;
import com.epsel.epsel_api.modules.ocr.entity.OCRScan;
import com.epsel.epsel_api.modules.ocr.repository.OCRScanRepository;
import com.epsel.epsel_api.modules.ocr.service.OCRService;
import com.epsel.epsel_api.modules.readings.entities.MeterReading;
import com.epsel.epsel_api.modules.readings.repositories.MeterReadingRepository;
import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.modules.supplies.repositories.SupplyRepository;
import com.epsel.epsel_api.shared.exceptions.BadRequestException;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import com.epsel.epsel_api.shared.storage.StorageService;
import com.epsel.epsel_api.shared.utils.MultipartInputStreamFileResource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OCRServiceImpl implements OCRService {

    private final OCRScanRepository repository;
    private final SupplyRepository supplyRepository;
    private final MeterReadingRepository meterReadingRepository;

    private final OCRProperties properties;
    private final RestClient restClient;

    private final StorageService imageService; // tu servicio existente

    @Override
    public OCRScanResponseDTO scan(
            UUID supplyId,
            MultipartFile file
    ) {

        Supply supply = supplyRepository.findById(supplyId)
                .orElseThrow(() -> new ResourceNotFoundException("Suministro no encontrado"));

        Integer previousReading =
                meterReadingRepository
                        .findTopBySupplyAndDeletedFalseOrderByReadingDateDesc(supply)
                        .map(MeterReading::getCurrentReading)
                        .orElse(0);

        String imageUrl =
                imageService.upload(file);

        OCRApiResponse response = callOCR(file);

        String rawTexts =
                String.join(
                        ", ",
                        response.getTexts()
                );

        if (response == null || response.getReading() == null) {
            throw new BadRequestException(
                    "No se pudo detectar una lectura válida en la imagen"
            );
        }

        Integer detected = response.getReading();

        Integer consumption = detected - previousReading;

        boolean anomaly = detected < previousReading || consumption > 500;

        OCRScan scan = new OCRScan();

        scan.setSupply(supply);
        scan.setImageUrl(imageUrl);
        scan.setPreviousReading(previousReading);
        scan.setDetectedReading(detected);
        scan.setConfidence(response.getConfidence());
        scan.setAnomaly(anomaly);
        scan.setScannedAt(LocalDateTime.now());
        scan.setRawDetectedTexts(rawTexts);

        OCRScan saved =
                repository.save(scan);

        return mapResponse(saved);
    }

    @Override
    public OCRScanResponseDTO getById(UUID id) {

        OCRScan scan =
                repository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Escaneo OCR no encontrado"
                                )
                        );

        return mapResponse(scan);
    }

    @Override
    public Page<OCRScanResponseDTO> getBySupply(
            UUID supplyId,
            Pageable pageable
    ) {

        return repository
                .findBySupplyIdAndDeletedFalse(
                        supplyId,
                        pageable
                )
                .map(this::mapResponse);
    }

    private OCRScanResponseDTO mapResponse(
            OCRScan scan
    ) {

        return OCRScanResponseDTO.builder()
                .id(scan.getId())
                .supplyId(scan.getSupply().getId())
                .supplyNumber(
                        scan.getSupply()
                                .getSupplyNumber()
                )
                .imageUrl(scan.getImageUrl())
                .previousReading(
                        scan.getPreviousReading()
                )
                .detectedReading(
                        scan.getDetectedReading()
                )
                .confidence(
                        scan.getConfidence()
                )
                .anomaly(
                        scan.getAnomaly()
                )
                .scannedAt(
                        scan.getScannedAt()
                )
                .observations(
                        scan.getObservations()
                )
                .detectedTexts(
                        scan.getRawDetectedTexts() == null
                                ? List.of()
                                : Arrays.stream(
                                scan.getRawDetectedTexts()
                                        .split(",")
                        ).toList()
                )
                .build();
    }

    private OCRApiResponse callOCR(
            MultipartFile file
    ) {

        try {

            MultiValueMap<String, Object> body =
                    new LinkedMultiValueMap<>();

            body.add(
                    "file",
                    new MultipartInputStreamFileResource(
                            file.getInputStream(),
                            file.getOriginalFilename()
                    )
            );

            return restClient.post()
                    .uri(properties.getUrl())
                    .contentType(
                            MediaType.MULTIPART_FORM_DATA
                    )
                    .body(body)
                    .retrieve()
                    .body(OCRApiResponse.class);

        } catch (Exception ex) {

            throw new BadRequestException(
                    "Error procesando OCR: "
                            + ex.getMessage()
            );
        }
    }

}