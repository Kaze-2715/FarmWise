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

        String brokerUri = environmentOrDefault("MQTT_BROKER_URI", config.brokerUri());
        String username = environmentOrDefault("MQTT_USERNAME", config.username());
        String password = environmentOrDefault("MQTT_PASSWORD", config.password());

        if (brokerUri == null || brokerUri.isBlank()) {
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
                                        brokerUri,
                                        username,
                                        password);
                            }

                            return new VirtualSensor(
                                    device.deviceId(),
                                    device.deviceType(),
                                    device.reportIntervalSeconds(),
                                    brokerUri,
                                    username,
                                    password,
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

    private static String environmentOrDefault(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
}
