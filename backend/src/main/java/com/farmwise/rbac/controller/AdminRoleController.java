package com.farmwise.rbac.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farmwise.rbac.dto.RoleResponse;
import com.farmwise.rbac.dto.UpdateRolePermissionsRequest;
import com.farmwise.rbac.service.RbacAdminService;
import com.farmwise.security.permission.RequiredPermission;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class AdminRoleController {
    private final RbacAdminService rbacAdminService;

    @GetMapping
    @RequiredPermission("role:read")
    public ResponseEntity<List<RoleResponse>> listRoles() {
        List<RoleResponse> response = rbacAdminService.listRoles();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{roleCode}/permissions")
    @RequiredPermission("role:manage")
    public ResponseEntity<RoleResponse> updateRolePermissions(
            @PathVariable String roleCode,
            @Valid @RequestBody UpdateRolePermissionsRequest request) {
        RoleResponse response = rbacAdminService.updateRolePermissions(
                roleCode,
                request);

        return ResponseEntity.ok(response);
    }
}
