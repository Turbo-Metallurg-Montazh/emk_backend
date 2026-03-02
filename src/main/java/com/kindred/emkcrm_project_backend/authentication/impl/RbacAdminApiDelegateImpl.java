package com.kindred.emkcrm_project_backend.authentication.impl;

import com.kindred.emkcrm_project_backend.api.RbacAdminApiDelegate;
import com.kindred.emkcrm_project_backend.authentication.rbac.RoleAdministrationService;
import com.kindred.emkcrm_project_backend.db.entities.Permission;
import com.kindred.emkcrm_project_backend.db.entities.Role;
import com.kindred.emkcrm_project_backend.model.AdminUserDto;
import com.kindred.emkcrm_project_backend.model.PermissionDto;
import com.kindred.emkcrm_project_backend.model.RoleDto;
import com.kindred.emkcrm_project_backend.model.UpdateRolePermissionsRequest;
import com.kindred.emkcrm_project_backend.model.UpdateUserRolesRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RbacAdminApiDelegateImpl implements RbacAdminApiDelegate {

    private final RoleAdministrationService roleAdministrationService;

    public RbacAdminApiDelegateImpl(RoleAdministrationService roleAdministrationService) {
        this.roleAdministrationService = roleAdministrationService;
    }

    @Override
    public ResponseEntity<List<PermissionDto>> listPermissions() {
        List<PermissionDto> permissions = roleAdministrationService.listPermissions().stream()
                .map(this::toPermissionDto)
                .toList();
        return new ResponseEntity<>(permissions, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<RoleDto>> listRolesWithPermissions() {
        List<RoleDto> roles = roleAdministrationService.listRolesWithPermissions().stream()
                .map(this::toRoleDto)
                .toList();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RoleDto> updateRolePermissions(String roleCode, UpdateRolePermissionsRequest request) {
        Role updatedRole = roleAdministrationService.updateRolePermissions(roleCode, request.getPermissions());
        return new ResponseEntity<>(toRoleDto(updatedRole), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AdminUserDto> updateUserRoles(String username, UpdateUserRolesRequest request) {
        AdminUserDto user = roleAdministrationService.updateUserRoles(username, request.getRoles());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    private PermissionDto toPermissionDto(Permission permission) {
        PermissionDto dto = new PermissionDto();
        dto.setCode(permission.getCode());
        dto.setDescription(permission.getDescription());
        return dto;
    }

    private RoleDto toRoleDto(Role role) {
        RoleDto dto = new RoleDto();
        dto.setCode(role.getCode());
        dto.setName(role.getName());
        dto.setSystem(role.isSystem());
        dto.setPermissions(role.getPermissions().stream()
                .map(Permission::getCode)
                .sorted()
                .toList());
        return dto;
    }
}
