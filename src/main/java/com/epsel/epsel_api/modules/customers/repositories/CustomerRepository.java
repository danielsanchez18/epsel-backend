package com.epsel.epsel_api.modules.customers.repositories;

import com.epsel.epsel_api.modules.customers.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends
        JpaRepository<Customer, UUID>,
        JpaSpecificationExecutor<Customer> {

    Optional<Customer> findByDocumentNumberAndDeletedFalse(String documentNumber);

    Boolean existsByDocumentNumberAndDeletedFalse(String documentNumber);

    Optional<Customer> findByIdAndDeletedFalse(UUID customerId);

}
