package com.farmwise.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserProfileRequest(
        @NotBlank(message = "用户名不能为空")
        @Size(min = 2, max = 20, message = "用户名大小应在 2 - 20 字符之间")
        String username,

        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式错误")
        @Size(max = 150, message = "邮箱应在 150 字符以内")
        String email,

        @Size(max = 30, message = "真实姓名应在 30 字符以内")
        String realName,

        @Pattern(regexp = "^1\\d{10}$", message = "手机号必须是1开头的11位数字")
        String phone,

        @Size(max = 50, message = "所属组织应在 50 字符以内")
        String organization,

        @Size(max = 20, message = "省份应在 20 字符以内")
        String province,

        @Size(max = 20, message = "城市应在 20 字符以内")
        String city,

        @Size(max = 50, message = "职务或身份应在 50 字符以内")
        String position,

        String avatarFileId,

        String verificationCode) {
}
