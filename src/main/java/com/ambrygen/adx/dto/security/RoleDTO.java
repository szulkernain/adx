package com.ambrygen.adx.dto.security;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class RoleDTO {
    private String id;
    private String name;
    private String description;
    private ZonedDateTime createdDate;
    private String createdBy;
    private ZonedDateTime lastModifiedDate;
    private String lastModifiedBy;
}
