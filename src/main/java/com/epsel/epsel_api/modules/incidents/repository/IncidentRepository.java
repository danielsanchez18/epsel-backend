package com.epsel.epsel_api.modules.incidents.repository;

import com.epsel.epsel_api.modules.incidents.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface IncidentRepository extends
        JpaRepository<Incident, UUID>,
        JpaSpecificationExecutor<Incident> {

    boolean existsByIncidentNumber(String incidentNumber);

    long countByDeletedFalse();

}