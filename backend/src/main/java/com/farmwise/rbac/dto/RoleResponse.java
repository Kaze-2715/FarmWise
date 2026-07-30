package com.farmwise.rbac.dto;

import java.util.List;

import com.farmwise.rbac.model.Role;

public record RoleResponse(
        String code,
        String name,
        List<String> permissions) {
    public static RoleResponse from(
            Role role,
            List<String> permissions) {
        return new RoleResponse(
                role.code(),
                role.name(),
                List.copyOf(permissions));
    }
}
