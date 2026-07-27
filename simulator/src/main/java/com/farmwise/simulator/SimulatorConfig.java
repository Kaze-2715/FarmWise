package com.farmwise.simulator;

import java.math.BigDecimal;
import java.util.List;

public record SimulatorConfig(
        String brokerUri, String username, String password, List<Device> devices) {
    public record Device(
            String deviceId,
            String deviceType,
            Long reportIntervalSeconds,
            BigDecimal initialBattery) {}
}
