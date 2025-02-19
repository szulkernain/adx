package com.ambrygen.adx.dto.security;

import lombok.Data;

import java.util.List;

@Data
public class UserListResponse {
    private int totalPages;
    private long totalCount;
    private int pageNumber;
    List<UserDTO> users;
}
