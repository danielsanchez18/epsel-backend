package com.epsel.epsel_api.modules.readings.specifications;

import com.epsel.epsel_api.modules.readings.entities.MeterReading;
import com.epsel.epsel_api.modules.readings.enums.ReadingStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.UUID;

public class MeterReadingSpecification {

    public static Specification<MeterReading> search(
            UUID supplyId,
            ReadingStatus status,
            LocalDate startDate,
            LocalDate endDate
    ) {

        return (root, query, cb) -> {

            var predicate = cb.conjunction();
            predicate = cb.and(predicate, cb.isFalse(root.get("deleted")));

            if (supplyId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("supply").get("id"), supplyId));
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
