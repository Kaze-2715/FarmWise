package com.farmwise.alert.dto;

import com.farmwise.task.dto.FarmTaskResponse;

public record StartAlertResponse(
        AlertResponse alert,
        FarmTaskResponse createdTask) {
}
