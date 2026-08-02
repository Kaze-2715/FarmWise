package com.farmwise.report.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record GenerateReportRequest(
        @NotBlank(message = "所属地块不能为空")
        String landId,

        @NotBlank(message = "报告类型不能为空")
        @Pattern(
                regexp = "comprehensive|device|environment|alert|task",
                message = "不支持的报告类型")
        String type,

        @NotBlank(message = "报告标题不能为空")
        @Size(max = 150, message = "报告标题不能超过 150 个字符")
        String title,

        @NotNull(message = "开始日期不能为空")
        LocalDate startDate,

        @NotNull(message = "结束日期不能为空")
        LocalDate endDate) {
}
