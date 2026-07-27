package com.farmwise.simulator;

import java.nio.file.Path;
import java.util.List;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

public class VirtualDeviceCluster {
    public static void main(String[] args) throws JacksonException {
        Path configPath = args.length > 0 ? Path.of(args[0]) : Path.of("devices.json");

        ObjectMapper objectMapper = JsonMapper.builder().build();

        SimulatorConfig config = objectMapper.readValue(configPath, SimulatorConfig.class);

        if (config == null) {
            throw new IllegalArgumentException("模拟器配置不能为空");
        }

        if (config.brokerUri() == null || config.brokerUri().isBlank()) {
            throw new IllegalArgumentException("brokerUri 不能为空");
        }

        if (config.devices() == null || config.devices().isEmpty()) {
            throw new IllegalArgumentException("devices 不能为空");
        }

        List<SimulatedDevice> devices =
                config.devices()
                        .stream()
                        .<SimulatedDevice>map(device -> {
                            if ("irrigation_controller".equals(device.deviceType())) {
                                return new VirtualIrrigationController(
                                        device.deviceId(),
                                        config.brokerUri(),
                                        config.username(),
                                        config.password());
                            }

                            return new VirtualSensor(
                                    device.deviceId(),
                                    device.deviceType(),
                                    device.reportIntervalSeconds(),
                                    config.brokerUri(),
                                    config.username(),
                                    config.password(),
                                    device.initialBattery());
                        })
                        .toList();

        try {
            devices.forEach(SimulatedDevice::start);
        } catch (RuntimeException exception) {
            devices.forEach(SimulatedDevice::stop);
            throw exception;
        }

        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> devices.forEach(SimulatedDevice::stop)));
    }
}
