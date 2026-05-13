package com.epsel.epsel_api.modules.customers.servicesImpl;

import com.epsel.epsel_api.modules.customers.dto.CreateCustomerDTO;
import com.epsel.epsel_api.modules.customers.dto.CustomerResponseDTO;
import com.epsel.epsel_api.modules.customers.dto.UpdateCustomerDTO;
import com.epsel.epsel_api.modules.customers.entities.Customer;
import com.epsel.epsel_api.modules.customers.enums.CustomerType;
import com.epsel.epsel_api.modules.customers.repositories.CustomerRepository;
import com.epsel.epsel_api.modules.customers.services.CustomerService;
import com.epsel.epsel_api.modules.customers.specifications.CustomerSpecification;
import com.epsel.epsel_api.shared.exceptions.BadRequestException;
import com.epsel.epsel_api.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;

    @Override
    public CustomerResponseDTO create(CreateCustomerDTO dto) {

        if (repository.existsByDocumentNumberAndDeletedFalse(dto.getDocumentNumber())) {
            throw new BadRequestException("Número de documento ya registrado");
        }

        Customer customer = new Customer();

        customer.setType(dto.getType());
        customer.setDocumentNumber(dto.getDocumentNumber());
        customer.setFullName(dto.getFullName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());

        Customer saved = repository.save(customer);
        return mapResponse(saved);
    }

    @Override
    public CustomerResponseDTO update(UUID id, UpdateCustomerDTO dto) {

        Customer customer = repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        if (dto.getFullName() != null) { customer.setFullName(dto.getFullName()); }
        if (dto.getPhone() != null) { customer.setPhone(dto.getPhone()); }
        if (dto.getEmail() != null) { customer.setEmail(dto.getEmail()); }

        Customer saved = repository.save(customer);
        return mapResponse(saved);
    }

    @Override
    public CustomerResponseDTO getById(UUID id) {

        Customer customer = repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        return mapResponse(customer);
    }

    @Override
    public Page<CustomerResponseDTO> search(String search, CustomerType type, Pageable pageable) {

        return repository.findAll(
                CustomerSpecification.search(search, type), pageable
        ).map(this::mapResponse);
    }

    @Override
    public void delete(UUID id) {

        Customer customer = repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        customer.setDeleted(true);
        repository.save(customer);
    }

    private CustomerResponseDTO mapResponse(Customer customer) {

        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .type(customer.getType())
                .documentNumber(customer.getDocumentNumber())
                .fullName(customer.getFullName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}