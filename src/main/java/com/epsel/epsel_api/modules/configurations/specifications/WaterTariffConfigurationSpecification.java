package com.epsel.epsel_api.modules.configurations.specifications;

import com.epsel.epsel_api.modules.configurations.entities.WaterTariffConfiguration;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class WaterTariffConfigurationSpecification {

    public static Specification<WaterTariffConfiguration> search(
            String zoneName,
            Boolean active
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isFalse(root.get("deleted")));

            if (zoneName != null && !zoneName.isBlank()) {
                String like = "%" + zoneName.toLowerCase() + "%";

                Predicate zoneNameLike = cb.like(cb.lower(root.get("zone").get("name")), like);
                predicates.add(zoneNameLike);
            }

            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
            }

            Predicate[] arr = predicates.toArray(new Predicate[0]);
            return cb.and(arr);
        };
    }
}