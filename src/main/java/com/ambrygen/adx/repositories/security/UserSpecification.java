package com.ambrygen.adx.repositories.security;


import com.ambrygen.adx.dto.security.UserListRequest;
import com.ambrygen.adx.models.security.User;
import com.ambrygen.adx.models.security.UserRole;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Based on - github.com/mbukowicz/spring-data-queries
 */
@Component
public class UserSpecification {

    public static Specification<User> getUsers(UserListRequest userListRequest, String roleId) {
        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();
            if (userListRequest.email() != null && !userListRequest.email().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("emailAddress")),
                        "%" + userListRequest.email().toLowerCase() + "%"));
            }
            if (userListRequest.firstName() != null && !userListRequest.firstName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")),
                        "%" + userListRequest.firstName().toLowerCase() + "%"));
            }
            if (userListRequest.lastName() != null && !userListRequest.lastName().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")),
                        "%" + userListRequest.lastName().toLowerCase() + "%"));
            }
            Predicate rolePredicate = null;
            if (null != roleId && !roleId.isBlank()) {
                Join<User, UserRole> userRole = root.join("userRoles");
                rolePredicate = criteriaBuilder.equal(userRole.get("role").get("id"), roleId);
            }

            String sortColumn = userListRequest.sortColumn();
            if (sortColumn == null || sortColumn.isEmpty()) {
                sortColumn = "firstName";
            }
            if ("desc".equalsIgnoreCase(userListRequest.sortDirection())) {
                query.orderBy(criteriaBuilder.desc(root.get(sortColumn)));
            } else {
                query.orderBy(criteriaBuilder.asc(root.get(sortColumn)));
            }
            Predicate finalPredicate = null;
            if (rolePredicate == null && predicates.isEmpty()) {
                finalPredicate = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
            if (rolePredicate != null && predicates.isEmpty()) {
                finalPredicate = rolePredicate;
            }
            if (rolePredicate != null && !predicates.isEmpty()) {
                Predicate orPredicate = criteriaBuilder.or(predicates.toArray(new Predicate[0]));
                finalPredicate = criteriaBuilder.and(rolePredicate, orPredicate);
            }
            if (rolePredicate == null && !predicates.isEmpty()) {
                finalPredicate = criteriaBuilder.or(predicates.toArray(new Predicate[0]));
            }
            return finalPredicate;
        };
    }
}
