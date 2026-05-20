package com.epsel.epsel_api.modules.supplies.specifications;

import com.epsel.epsel_api.modules.supplies.entities.Supply;
import com.epsel.epsel_api.modules.supplies.enums.SupplyStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SupplySpecification {

    public static Specification<Supply> search(
            String search,
            SupplyStatus status,
            UUID zoneId
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates =
                    new ArrayList<>();

            predicates.add(
                    cb.isFalse(root.get("deleted"))
            );

            if (
                    search != null &&
                            !search.isBlank()
            ) {

                Join<Object, Object> customer = root.join("customer");
                Join<Object, Object> property = root.join("property");

                String like = "%" + search.toLowerCase() + "%";

                predicates.add(
                        cb.or(

                                cb.like(
                                        cb.lower(
                                                customer.get("fullName")
                                        ),
                                        like
                                ),

                                cb.like(
                                        cb.lower(
                                                customer.get("documentNumber")
                                        ),
                                        like
                                ),

                                cb.like(
                                        cb.lower(
                                                property.get("address")
                                        ),
                                        like
                                ),

                                cb.like(
                                        cb.lower(
                                                root.get("supplyNumber")
                                        ),
                                        like
                                ),

                                cb.like(
                                        cb.lower(
                                                root.get("meterNumber")
                                        ),
                                        like
                                )
                        )
                );
            }

            if (status != null) {

                predicates.add(
                        cb.equal(
                                root.get("status"),
                                status
                        )
                );
            }

            if (zoneId != null) {

                predicates.add(
                        cb.equal(
                                root.get("property")
                                        .get("zone")
                                        .get("id"),
                                zoneId
                        )
                );
            }

            query.orderBy(
                    cb.desc(root.get("createdAt"))
            );

            return cb.and(
                    predicates.toArray(
                            new Predicate[0]
                    )
            );
        };
    }
}