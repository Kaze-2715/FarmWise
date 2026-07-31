package com.farmwise.alert.dto;

import java.time.LocalDateTime;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAlertRequest(
        @NotBlank(message = "所属地块不能为空")
        String landId,

        @NotBlank(message = "预警类型不能为空")
        String type,

        @NotBlank(message = "预警等级不能为空")
        String severity,

        @NotBlank(message = "预警标题不能为空")
        @Size(max = 150, message = "预警标题不能超过 150 个字符")
        String title,

        @NotBlank(message = "预警描述不能为空")
        @Size(max = 1000, message = "预警描述不能超过 1000 个字符")
        String description,

        @NotBlank(message = "处理建议不能为空")
        @Size(max = 1000, message = "处理建议不能超过 1000 个字符")
        String suggestion,

        @NotNull(message = "预警发生时间不能为空")
        LocalDateTime occurredAt,

        @Valid
        @NotNull(message = "预警来源不能为空")
        AlertSourceRequest source) {
}
