package com.farmwise.alert.dto;

import java.time.LocalDateTime;

public record LatestAlertStateRow(
        String id,
        String status,
        String sourceDeviceId,
        LocalDateTime handledAt) {

}
