package com.epsel.epsel_api.modules.readings.servicesImpl;

import com.epsel.epsel_api.modules.readings.dto.OcrResponseDTO;
import com.epsel.epsel_api.modules.readings.services.OcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class OcrServiceImpl implements OcrService {

    @Value("${ocr.url}")
    private String ocrUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public OcrResponseDTO processMeterImage(MultipartFile file) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            
            // Convertir el MultipartFile en un recurso que RestTemplate pueda enviar
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename() != null ? file.getOriginalFilename() : "image.jpg";
                }
            };
            
            body.add("file", resource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<OcrResponseDTO> response = restTemplate.postForEntity(ocrUrl, requestEntity, OcrResponseDTO.class);

            return response.getBody();
            
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la imagen con OCR: " + e.getMessage(), e);
        }
    }
}
