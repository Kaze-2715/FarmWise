package com.farmwise.device.capability;

import java.util.Map;
import java.util.Set;

public final class DeviceMetricCapabilities {
    private static final String BATTERY_METRIC = "battery";

    private static final Map<String, Set<String>> SENSOR_METRICS_BY_DEVICE_TYPE =
            Map.of(
                    "soil_moisture_sensor", Set.of("soil_moisture"),
                    "air_temp_humidity_sensor", Set.of("air_temperature", "air_humidity"),
                    "light_sensor", Set.of("light"),
                    "soil_ph_sensor", Set.of("soil_ph"),
                    "pest_camera", Set.of(),
                    "irrigation_controller", Set.of());

    private DeviceMetricCapabilities() {
    }

    public static Set<String> sensorMetricsForDeviceType(String deviceType) {
        return SENSOR_METRICS_BY_DEVICE_TYPE.get(deviceType);
    }

    public static boolean isSupportedMetric(String metric) {
        if (metric == null) {
            return false;
        }
        if (BATTERY_METRIC.equals(metric)) {
            return true;
        }
        return SENSOR_METRICS_BY_DEVICE_TYPE.values()
                .stream()
                .anyMatch(metrics -> metrics.contains(metric));
    }

    public static boolean supportsMetric(String deviceType, String metric) {
        if (metric == null) {
            return false;
        }
        Set<String> sensorMetrics = sensorMetricsForDeviceType(deviceType);
        if (sensorMetrics == null) {
            return false;
        }
        return BATTERY_METRIC.equals(metric) || sensorMetrics.contains(metric);
    }
}
