package com.ambrygen.adx.controllers.security;

import com.ambrygen.adx.dto.security.RoleListRequest;
import com.ambrygen.adx.dto.security.RoleListResponse;
import com.ambrygen.adx.services.security.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;


    @PostMapping(value = "/list", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleListResponse> getRoleList(@RequestBody RoleListRequest roleListRequest) {
        RoleListResponse roleListResponse = roleService.getRoleList(roleListRequest);
        return ResponseEntity.status(HttpStatus.OK).body(roleListResponse);
    }
}
