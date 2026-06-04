package com.epsel.epsel_api.modules.customers.repositories;

import com.epsel.epsel_api.modules.customers.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends
        JpaRepository<Customer, UUID>,
        JpaSpecificationExecutor<Customer> {

    Optional<Customer> findByDocumentNumberAndDeletedFalse(String documentNumber);

    Boolean existsByDocumentNumberAndDeletedFalse(String documentNumber);

    Optional<Customer> findByIdAndDeletedFalse(UUID customerId);

    long countByDeletedFalse();

    @Query("SELECT COUNT(c) FROM Customer c WHERE MONTH(c.createdAt) = :month AND YEAR(c.createdAt) = :year AND c.deleted = false")
    long countCreatedInMonth(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(DISTINCT s.customer) FROM Supply s WHERE s.status = 'ACTIVE' AND s.deleted = false")
    long countActiveCustomers();

    long countByDeletedFalseAndCreatedAtAfter(java.time.LocalDateTime dateTime);
}
