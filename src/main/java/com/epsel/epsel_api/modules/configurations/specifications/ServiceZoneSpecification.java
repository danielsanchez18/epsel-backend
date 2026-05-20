package com.epsel.epsel_api.modules.configurations.specifications;

import com.epsel.epsel_api.modules.configurations.entities.ServiceZone;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ServiceZoneSpecification {

    public static Specification<ServiceZone> search(
            String search,
            Boolean active
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Siempre excluir eliminados
            predicates.add(cb.isFalse(root.get("deleted")));

            if (search != null && !search.isBlank()) {

                String like = "%" + search.toLowerCase() + "%";

                Predicate nameLike = cb.like(cb.lower(root.get("name")), like);
                Predicate descLike = cb.like(cb.lower(root.get("description")), like);

                predicates.add(cb.or(nameLike, descLike));
            }

            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
            }

            Predicate[] arr = predicates.toArray(new Predicate[0]);
            return cb.and(arr);
        };
    }
}
