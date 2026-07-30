package com.farmwise.rbac.dto;

import com.farmwise.rbac.model.Permission;

public record PermissionResponse(
        String code,
        String name,
        String module) {
    public static PermissionResponse from(Permission permission) {
        return new PermissionResponse(
                permission.code(),
                permission.name(),
                permission.module());
    }
}
