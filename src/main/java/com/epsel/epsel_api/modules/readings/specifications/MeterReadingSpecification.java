package com.epsel.epsel_api.modules.readings.specifications;

import com.epsel.epsel_api.modules.readings.entities.MeterReading;
import com.epsel.epsel_api.modules.readings.enums.ReadingStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public class MeterReadingSpecification {

    public static Specification<MeterReading> search(
            String search,
            UUID zoneId,
            ReadingStatus status,
            LocalDate startDate,
            LocalDate endDate
    ) {

        return (root, query, cb) -> {

            var predicate = cb.conjunction();

            predicate = cb.and(predicate, cb.isFalse(root.get("deleted")));

            if (search != null && !search.isBlank()) {

                String like = "%" + search.toLowerCase() + "%";

                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("supply").get("supplyNumber")), like),
                        cb.like(cb.lower(root.get("supply").get("meterNumber")), like),
                        cb.like(cb.lower(root.get("supply").get("customer").get("fullName")), like))
                );
            }

            if (zoneId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("supply").get("property").get("zone").get("id"), zoneId));
            }

            if (status != null) {
                predicate = cb.and(predicate, cb.equal(root.get("status"), status));
            }

            if (startDate != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("readingDate"), startDate));
            }

            if (endDate != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("readingDate"), endDate));
            }

            return predicate;
        };
    }
}