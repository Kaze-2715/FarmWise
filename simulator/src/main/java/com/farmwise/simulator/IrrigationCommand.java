package com.farmwise.simulator;

import java.time.Instant;

public record IrrigationCommand(
        String recordId, String action, Integer plannedDuration, Instant issuedAt) {}
