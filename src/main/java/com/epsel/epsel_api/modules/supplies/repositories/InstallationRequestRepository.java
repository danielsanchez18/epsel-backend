package com.epsel.epsel_api.modules.supplies.repositories;

import com.epsel.epsel_api.modules.supplies.entities.InstallationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InstallationRequestRepository extends JpaRepository<InstallationRequest, UUID> {
}
