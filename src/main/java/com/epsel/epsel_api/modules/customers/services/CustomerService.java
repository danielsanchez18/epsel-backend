package com.epsel.epsel_api.modules.customers.services;

import com.epsel.epsel_api.modules.customers.dto.CreateCustomerDTO;
import com.epsel.epsel_api.modules.customers.dto.CustomerResponseDTO;
import com.epsel.epsel_api.modules.customers.dto.UpdateCustomerDTO;
import com.epsel.epsel_api.modules.customers.enums.CustomerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomerService {

    CustomerResponseDTO create(CreateCustomerDTO dto);

    CustomerResponseDTO update(UUID id, UpdateCustomerDTO dto);

    CustomerResponseDTO getById(UUID id);

    Page<CustomerResponseDTO> search(
            String search,
            CustomerType type,
            Pageable pageable
    );

    void delete(UUID id);

    com.epsel.epsel_api.modules.customers.dto.CustomerKpisDTO getKpis();

    com.epsel.epsel_api.modules.customers.dto.CustomerDetailKpisDTO getDetailKpis(UUID customerId);

    com.epsel.epsel_api.shared.responses.ImportPreviewResponse<CreateCustomerDTO> previewImport(org.springframework.web.multipart.MultipartFile file);

    void createBulk(java.util.List<CreateCustomerDTO> dtos);
}