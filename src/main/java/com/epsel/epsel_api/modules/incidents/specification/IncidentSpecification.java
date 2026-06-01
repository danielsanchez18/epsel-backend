package com.epsel.epsel_api.modules.incidents.specification;

import com.epsel.epsel_api.modules.incidents.entity.Incident;
import com.epsel.epsel_api.modules.incidents.enums.IncidentPriority;
import com.epsel.epsel_api.modules.incidents.enums.IncidentStatus;
import com.epsel.epsel_api.modules.incidents.enums.IncidentType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public class IncidentSpecification {

    public static Specification<Incident> search(
            IncidentStatus status,
            IncidentPriority priority,
            IncidentType type,
            UUID customerId,
            UUID supplyId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        return (root, query, cb) -> {

            var predicate = cb.conjunction();

            predicate = cb.and(
                    predicate,
                    cb.isFalse(root.get("deleted"))
            );

            if (status != null) {
                predicate = cb.and(
                        predicate,
                        cb.equal(root.get("status"), status)
                );
            }

            if (priority != null) {
                predicate = cb.and(
                        predicate,
                        cb.equal(root.get("priority"), priority)
                );
            }

            if (type != null) {
                predicate = cb.and(
                        predicate,
                        cb.equal(root.get("type"), type)
                );
            }

            if (customerId != null) {
                predicate = cb.and(
                        predicate,
                        cb.equal(
                                root.get("customer").get("id"),
                                customerId
                        )
                );
            }

            if (supplyId != null) {
                predicate = cb.and(
                        predicate,
                        cb.equal(
                                root.get("supply").get("id"),
                                supplyId
                        )
                );
            }

            if (startDate != null) {
                predicate = cb.and(
                        predicate,
                        cb.greaterThanOrEqualTo(
                                root.get("reportedDate"),
                                startDate
                        )
                );
            }

            if (endDate != null) {
                predicate = cb.and(
                        predicate,
                        cb.lessThanOrEqualTo(
                                root.get("reportedDate"),
                                endDate
                        )
                );
            }

            return predicate;
        };
    }
}