package com.farmwise.simulator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TelemetryPayload(
        UUID messageId, Instant reportedAt, BigDecimal battery, List<Reading> readings) {
    public record Reading(String metric, BigDecimal value) {}
}
