package com.ambrygen.adx.dto.security;

public record RoleListRequest(String name,
        String description,
        Integer pageNumber,
        Integer pageSize,
        String sortDirection,
        String sortColumn) { }
