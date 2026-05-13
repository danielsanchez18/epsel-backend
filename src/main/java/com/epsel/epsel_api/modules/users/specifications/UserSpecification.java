package com.epsel.epsel_api.modules.users.specifications;

import com.epsel.epsel_api.modules.users.dto.UserSearchDTO;
import com.epsel.epsel_api.modules.users.entities.User;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> filter(UserSearchDTO searchDTO) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            /* SEARCH */
            if (searchDTO.getSearch() != null && !searchDTO.getSearch().isBlank()) {
                String search = "%" + searchDTO.getSearch().toLowerCase() + "%";

                // Concatenar names y lastNames con un espacio intermedio
                Expression<String> fullName = cb.concat(cb.concat(root.get("names"), " "), root.get("lastNames"));

                Predicate fullNamePredicate = cb.like(cb.lower(fullName), search);
                Predicate emailPredicate = cb.like(cb.lower(root.get("email")), search);
                Predicate dniPredicate = cb.like(cb.lower(root.get("dni")), search);

                predicates.add(cb.or(
                        fullNamePredicate,
                        emailPredicate,
                        dniPredicate)
                );
            }

            /* STATUS */
            if (searchDTO.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), searchDTO.getStatus()));
            }

            /* ROLE */
            if (searchDTO.getRole() != null) {
                predicates.add(cb.equal(root.get("role").get("name"), searchDTO.getRole()));
            }

            /* START DATE */
            if (searchDTO.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), searchDTO.getStartDate().atStartOfDay()));
            }

            /* END DATE */
            if (searchDTO.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), searchDTO.getEndDate().atTime(23, 59, 59)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}