package com.farmwise.alert.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record IgnoreAlertRequest(
        @NotBlank(message = "忽略原因不能为空")
        @Size(max = 1000, message = "忽略原因不能超过 1000 个字符")
        String remark) {
}
