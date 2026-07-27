package com.farmwise.irrigation.mqtt;

import java.time.Instant;

public record IrrigationCommand(
        String recordId, String action, Integer plannedDuration, Instant issuedAt) {}
