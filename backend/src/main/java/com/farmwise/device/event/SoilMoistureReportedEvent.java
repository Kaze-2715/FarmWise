package com.farmwise.device.event;

import java.math.BigDecimal;
import java.time.Instant;

public record SoilMoistureReportedEvent(
        String messageId, String deviceId, String landId, BigDecimal moisture, Instant reportedAt) {

}
