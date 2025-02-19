package com.ambrygen.adx.repositories.security;


import com.ambrygen.adx.dto.security.RoleListRequest;
import com.ambrygen.adx.models.security.Role;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Based on - github.com/mbukowicz/spring-data-queries
 */
@Component
public class RoleSpecification {

    public static Specification<Role> getRoles(RoleListRequest roleListRequest) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (roleListRequest.name() != null && !roleListRequest.name().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                        "%" + roleListRequest.name().toLowerCase() + "%"));
            }
            if (roleListRequest.description() != null && !roleListRequest.description().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("description")),
                        "%" + roleListRequest.description().toLowerCase() + "%"));
            }

            String sortColumn = roleListRequest.sortColumn();
            if (sortColumn == null || sortColumn.isEmpty()) {
                sortColumn = "name";
            }
            if ("desc".equalsIgnoreCase(roleListRequest.sortDirection())) {
                query.orderBy(criteriaBuilder.desc(root.get(sortColumn)));
            } else {
                query.orderBy(criteriaBuilder.asc(root.get(sortColumn)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
