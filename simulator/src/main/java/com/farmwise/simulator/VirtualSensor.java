package com.farmwise.simulator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

public class VirtualSensor implements SimulatedDevice, MqttCallbackExtended {
    private final String deviceId;
    private final String deviceType;
    private final long reportIntervalSeconds;
    private final String brokerUri;
    private final String username;
    private final String password;
    private BigDecimal battery;

    private MqttClient client;

    private ScheduledExecutorService scheduler;

    private final ObjectMapper objectMapper =
            JsonMapper.builder().disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS).build();

    public VirtualSensor(
            String deviceId,
            String deviceType,
            long reportIntervalSeconds,
            String brokerUri,
            String username,
            String password,
            BigDecimal initialBattery) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.reportIntervalSeconds = reportIntervalSeconds;
        this.brokerUri = brokerUri;
        this.username = username;
        this.password = password;
        this.battery = initialBattery;
    }

    @Override
    public void connectComplete(boolean reconnect, String serverUri) {
        String statusTopic = "farmwise/v1/devices/%s/status".formatted(deviceId);

        try {
            client.publish(
                    statusTopic,
                    "{\"status\":\"online\"}".strip().getBytes(StandardCharsets.UTF_8),
                    1,
                    true);
        } catch (MqttException exception) {
            throw new IllegalStateException("发布虚拟设备在线状态失败：" + deviceId, exception);
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.err.println(
                "虚拟设备连接断开, 设备ID %s, 原因 %s".formatted(deviceId, cause.getMessage()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {}

    public void start() {
        try {
            String clientId = "farmwise-simulator-" + deviceId;
            String statusTopic = "farmwise/v1/devices/%s/status".formatted(deviceId);

            client = new MqttClient(brokerUri, clientId, new MemoryPersistence());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(30);
            options.setWill(statusTopic, """
                    {
                "status" : "offline"}
                    """.strip().getBytes(StandardCharsets.UTF_8), 1, true);

            if (username != null && !username.isBlank()) {
                options.setUserName(username);
                options.setPassword(password.toCharArray());
            }

            client.setCallback(this);
            client.connect(options);
        } catch (MqttException exception) {
            throw new IllegalStateException("虚拟设备连接 EMQX 失败: " + deviceId, exception);
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(
                this::publishTelemetry, 0, reportIntervalSeconds, TimeUnit.SECONDS);
    }

    private void publishTelemetry() {
        try {
            if (client == null || !client.isConnected()) {
                return;
            }

            ThreadLocalRandom random = ThreadLocalRandom.current();

            List<TelemetryPayload.Reading> readings = switch (deviceType) {
                case "soil_moisture_sensor" -> List.of(
                        new TelemetryPayload.Reading(
                                "soil_moisture",
                                BigDecimal.valueOf(
                                        random.nextDouble(30, 80)).setScale(2, RoundingMode.HALF_UP)));

                case "air_temp_humidity_sensor" -> List.of(
                        new TelemetryPayload.Reading(
                                "air_temperature",
                                BigDecimal.valueOf(
                                        random.nextDouble(15, 35)).setScale(2, RoundingMode.HALF_UP)),
                        new TelemetryPayload.Reading(
                                "air_humidity",
                                BigDecimal.valueOf(
                                        random.nextDouble(40, 90)).setScale(2, RoundingMode.HALF_UP)));

                case "light_sensor" -> List.of(
                        new TelemetryPayload.Reading(
                                "light",
                                BigDecimal.valueOf(
                                        random.nextDouble(1_000, 80_000)).setScale(2, RoundingMode.HALF_UP)));

                case "soil_ph_sensor" -> List.of(
                        new TelemetryPayload.Reading(
                                "soil_ph",
                                BigDecimal.valueOf(
                                        random.nextDouble(5.5, 8.0)).setScale(2, RoundingMode.HALF_UP)));

                default -> throw new IllegalStateException(
                        "不支持生成遥测数据的设备类型：" + deviceType);
            };

            battery = battery.subtract(
                    BigDecimal.valueOf(
                            random.nextDouble(0.01, 0.05)))
                    .max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

            if (battery.compareTo(new BigDecimal("30")) < 0) {
                battery = new BigDecimal("100");

                System.out.println("虚拟设备已更换电池, deviceId=" + deviceId);
            }

            TelemetryPayload payload = new TelemetryPayload(
                    UUID.randomUUID(),
                    Instant.now(),
                    battery,
                    readings);

            String topic = "farmwise/v1/devices/%s/telemetry".formatted(deviceId);

            client.publish(topic,
                    objectMapper.writeValueAsBytes(payload),
                    1,
                    false);

            System.out.println("虚拟设备已上报数据, deviceId=%s, type=%s".formatted(deviceId, deviceType));
        } catch (Exception exception) {
            System.err.println("虚拟设备上报数据失败, deviceId=%s, reason=%s".formatted(deviceId, exception.getMessage()));
        }
    }

    public void stop() {
        if (scheduler != null) {
            scheduler.shutdown();

            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException exception) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        if (client == null) {
            return;
        }

        String statusTopic = "farmwise/v1/devices/%s/status".formatted(deviceId);

        try {
            if (client.isConnected()) {
                client.publish(
                        statusTopic,
                        "{\"status\":\"offline\"}"
                                .strip()
                                .getBytes(StandardCharsets.UTF_8),
                        1,
                        true);
            }
        } catch (MqttException exception) {
            System.err.println("发布虚拟设备离线状态失败，设备 ID：" + deviceId);
        }

        try {
            if (client.isConnected()) {
                client.disconnect(5_000);
            }
        } catch (MqttException exception) {
            System.err.println("断开虚拟设备 MQTT 连接失败，设备 ID：" + deviceId);
        }

        try {
            client.close();
        } catch (MqttException exception) {
            System.err.println("关闭虚拟设备 MQTT 客户端失败，设备 ID：" + deviceId);
        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {

    }
}
