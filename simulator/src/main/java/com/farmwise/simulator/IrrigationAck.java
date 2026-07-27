package com.farmwise.simulator;

import java.math.BigDecimal;
import java.time.Instant;

public record IrrigationAck(
        String recordId,
        String action,
        String status,
        Instant occurredAt,
        Integer duration,
        BigDecimal waterUsage,
        String reason) {}
