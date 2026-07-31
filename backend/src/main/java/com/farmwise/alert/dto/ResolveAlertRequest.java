package com.farmwise.alert.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResolveAlertRequest(
        @NotBlank(message = "处理措施不能为空")
        @Size(max = 1000, message = "处理措施不能超过 1000 个字符")
        String measure,

        @NotNull(message = "处理时间不能为空")
        LocalDateTime handledAt,

        @NotBlank(message = "处理结果不能为空")
        @Size(max = 1000, message = "处理结果不能超过 1000 个字符")
        String result,

        @Size(max = 1000, message = "备注不能超过 1000 个字符")
        String remark) {
}
