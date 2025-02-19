package com.ambrygen.adx.dto.security;

public record UserListRequest(String firstName,
                             String lastName,
                             String email,
                             String role,
                             Integer pageNumber,
                             Integer pageSize,
                             String sortDirection,
                             String sortColumn) { }
