package com.farmwise.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequest(
        @NotBlank(message = "登录类型不能为空")
        @Pattern(regexp = "email|username", message = "登录类型不合法")
        String loginType,

        @NotBlank(message = "登录账号不能为空")
        String account,

        @NotBlank(message = "密码不能为空")
        String password) {
}
