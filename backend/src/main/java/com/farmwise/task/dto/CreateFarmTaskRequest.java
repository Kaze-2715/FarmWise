package com.farmwise.task.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateFarmTaskRequest(
        @NotBlank(message = "所属地块不能为空")
        String landId,

        @NotBlank(message = "任务类型不能为空")
        @Pattern(
                regexp = "irrigation|fertilization|pesticide|weeding|inspection|harvest|other",
                message = "不支持的任务类型")
        String taskType,

        @NotBlank(message = "任务标题不能为空")
        @Size(max = 150, message = "任务标题不能超过 150 个字符")
        String title,

        @NotBlank(message = "任务描述不能为空")
        @Size(max = 1000, message = "任务描述不能超过 1000 个字符")
        String description,

        @NotBlank(message = "任务优先级不能为空")
        @Pattern(regexp = "low|medium|high", message = "不支持的任务优先级")
        String priority,

        @NotBlank(message = "负责人不能为空")
        String assigneeId,

        @NotNull(message = "截止时间不能为空")
        LocalDateTime deadline,

        @Size(max = 1000, message = "备注不能超过 1000 个字符")
        String remark) {
}
