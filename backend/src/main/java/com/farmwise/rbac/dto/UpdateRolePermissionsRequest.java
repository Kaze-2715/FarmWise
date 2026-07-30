package com.farmwise.rbac.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateRolePermissionsRequest(
        @NotNull(message = "权限编码列表不能为空")
        List<@NotBlank(message = "权限编码不能为空") String> permissionCodes) {
}
