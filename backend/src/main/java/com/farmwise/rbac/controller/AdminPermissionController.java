package com.farmwise.rbac.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.farmwise.rbac.dto.PermissionResponse;
import com.farmwise.rbac.service.RbacAdminService;
import com.farmwise.security.permission.RequiredPermission;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/permissions")
@RequiredArgsConstructor
public class AdminPermissionController {
    private final RbacAdminService rbacAdminService;

    @GetMapping
    @RequiredPermission("role:read")
    public ResponseEntity<List<PermissionResponse>> listPermissions(
            @RequestParam(required = false) String module) {
        List<PermissionResponse> response =
                rbacAdminService.listPermissions(module);

        return ResponseEntity.ok(response);
    }
}
