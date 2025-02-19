package com.ambrygen.adx.services.security;

import com.ambrygen.adx.dto.security.RoleDTO;
import com.ambrygen.adx.dto.security.RoleListRequest;
import com.ambrygen.adx.dto.security.RoleListResponse;
import com.ambrygen.adx.models.security.Role;
import com.ambrygen.adx.repositories.security.RoleRepository;
import com.ambrygen.adx.repositories.security.RoleSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleSpecification roleSpecification;

    @Value("${pagination.page.size.default: 25}")
    private Integer defaultPageSize;


    public RoleListResponse getRoleList(RoleListRequest request) {
        List<Role> list = null;
        Page<Role> pages = null;
        if (request.pageNumber() == null) {
            pages = new PageImpl<>(roleRepository.findAll(roleSpecification.getRoles(request)));
        } else {
            Integer pageSize = 0;
            if (request.pageSize() == null) {
                pageSize = defaultPageSize;
            } else {
                pageSize = request.pageSize();
            }
            Pageable paging = PageRequest.of(request.pageNumber(), request.pageSize());
            pages = roleRepository.findAll(roleSpecification.getRoles(request), paging);
        }
        if (pages != null && pages.getContent() != null) {
            list = pages.getContent();
            if (list != null && list.size() > 0) {
                RoleListResponse respList = new RoleListResponse();
                respList.setTotalPages(pages.getTotalPages());
                respList.setTotalCount(pages.getTotalElements());
                respList.setPageNumber(pages.getNumber());
                List<RoleDTO> roleDTOList = getRoleDTOList(list);
                respList.setRoles(roleDTOList);
                return respList;
            }
        }
        return null;
    }

    private List<RoleDTO> getRoleDTOList(List<Role> roles) {
        List<RoleDTO> roleDTOList = new ArrayList<>();
        for (Role role : roles) {
            RoleDTO roleDTO = getRoleDTO(role);
            roleDTOList.add(roleDTO);
        }
        return roleDTOList;
    }

    private RoleDTO getRoleDTO(Role role) {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(role.getId());
        roleDTO.setName(role.getName().name());
        roleDTO.setDescription(role.getDescription());
        roleDTO.setCreatedBy(role.getCreatedBy());
        roleDTO.setCreatedDate(role.getCreatedDate());
        roleDTO.setLastModifiedBy(role.getLastModifiedBy());
        roleDTO.setLastModifiedDate(role.getLastModifiedDate());
        return roleDTO;
    }
}
