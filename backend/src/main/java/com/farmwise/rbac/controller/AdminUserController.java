package com.farmwise.rbac.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.farmwise.rbac.dto.UpdateUserRolesRequest;
import com.farmwise.rbac.service.RbacAdminService;
import com.farmwise.security.permission.RequiredPermission;
import com.farmwise.user.dto.UserProfile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final RbacAdminService rbacAdminService;

    @GetMapping
    @RequiredPermission("user:read")
    public ResponseEntity<List<UserProfile>> listUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String roleCode) {
        List<UserProfile> response = rbacAdminService.listUsers(
                keyword,
                status,
                roleCode);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}/roles")
    @RequiredPermission("user:grant")
    public ResponseEntity<UserProfile> updateUserRoles(
            @PathVariable String userId,
            @Valid @RequestBody UpdateUserRolesRequest request,
            Authentication authentication) {
        String operatorId = authentication.getName();
        UserProfile response = rbacAdminService.updateUserRoles(
                operatorId,
                userId,
                request);

        return ResponseEntity.ok(response);
    }
}
