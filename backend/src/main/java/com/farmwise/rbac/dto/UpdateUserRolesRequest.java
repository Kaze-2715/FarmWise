package com.farmwise.rbac.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record UpdateUserRolesRequest(
        @NotEmpty(message = "用户至少需要保留一个角色")
        List<@NotBlank(message = "角色编码不能为空") String> roleCodes) {
}
