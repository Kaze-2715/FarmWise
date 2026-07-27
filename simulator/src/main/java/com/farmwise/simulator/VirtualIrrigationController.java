package com.farmwise.simulator;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

public class VirtualIrrigationController implements SimulatedDevice, MqttCallbackExtended {
    private static final BigDecimal WATER_USAGE_PER_MINUTE = new BigDecimal("2.500");
    private static final String ACK_TOPIC_TEMPLATE = "farmwise/v1/devices/%s/command-acks";

    private final String deviceId;
    private final String brokerUri;
    private final String username;
    private final String password;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> completionTask;
    private String activeRecordId;
    private Instant irrigationStartedAt;

    private MqttClient client;

    private final ObjectMapper objectMapper =
            JsonMapper.builder().disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS).build();

    public VirtualIrrigationController(
            String deviceId, String brokerUri, String username, String password) {
        this.deviceId = deviceId;
        this.brokerUri = brokerUri;
        this.username = username;
        this.password = password;
    }

    @Override
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
            options.setWill(
                    statusTopic,
                    "{\"status\":\"offline\"}".getBytes(StandardCharsets.UTF_8),
                    1,
                    true);

            if (username != null && !username.isBlank()) {
                options.setUserName(username);
                options.setPassword(password.toCharArray());
            }

            client.setCallback(this);
            client.connect(options);
        } catch (MqttException exception) {
            throw new IllegalStateException("虚拟灌溉控制器连接 EMQX 失败：" + deviceId, exception);
        }
    }

    @Override
    public void connectComplete(boolean reconnect, String serverUri) {
        String commandTopic = "farmwise/v1/devices/%s/commands".formatted(deviceId);
        String statusTopic = "farmwise/v1/devices/%s/status".formatted(deviceId);

        try {
            client.subscribe(commandTopic, 1);

            client.publish(
                    statusTopic,
                    "{\"status\":\"online\"}".getBytes(StandardCharsets.UTF_8),
                    1,
                    true);
        } catch (MqttException exception) {
            throw new IllegalStateException(
                    "初始化虚拟灌溉控制器 MQTT 连接失败：" + deviceId, exception);
        }

        System.out.println(
                "虚拟灌溉控制器已连接并订阅命令，deviceId=%s, topic=%s, reconnect=%s".formatted(
                        deviceId, commandTopic, reconnect));
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        try {
            IrrigationCommand command = parseCommand(topic, message.getPayload());

            switch (command.action()) {
                case "start" -> startIrrigation(command);
                case "stop" -> stopIrrigation(command);
                default -> throw new IllegalArgumentException("不支持的灌溉动作：" + command.action());
            }
        } catch (Exception exception) {
            System.err.println("虚拟灌溉控制器处理命令失败，deviceId=%s, reason=%s"
                    .formatted(
                            deviceId,
                            exception.getMessage()));
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.err.println(
                "虚拟灌溉控制器连接断开，deviceId=%s, reason=%s"
                        .formatted(
                                deviceId,
                                cause.getMessage()));
    }

    @Override
    public void deliveryComplete(
            IMqttDeliveryToken token) {
    }

    @Override
    public void stop() {
        if (completionTask != null) {
            completionTask.cancel(false);
        }
        scheduler.shutdown();

        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException exception) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        String statusTopic = "farmwise/v1/devices/%s/status"
                .formatted(deviceId);

        if (client == null) {
            return;
        }

        try {
            if (client.isConnected()) {
                client.publish(
                        statusTopic,
                        "{\"status\":\"offline\"}"
                                .getBytes(StandardCharsets.UTF_8),
                        1,
                        true);
            }
        } catch (MqttException exception) {
            System.err.println(
                    "发布虚拟灌溉控制器离线状态失败，deviceId="
                            + deviceId);
        }

        try {
            if (client.isConnected()) {
                client.disconnect(5_000);
            }
        } catch (MqttException exception) {
            System.err.println(
                    "断开虚拟灌溉控制器 MQTT 连接失败，deviceId="
                            + deviceId);
        }

        try {
            client.close();
        } catch (MqttException exception) {
            System.err.println(
                    "关闭虚拟灌溉控制器 MQTT 客户端失败，deviceId="
                            + deviceId);
        }
    }

    private IrrigationCommand parseCommand(
            String topic,
            byte[] payload) {
        String expectedTopic = "farmwise/v1/devices/%s/commands"
                .formatted(deviceId);

        if (!expectedTopic.equals(topic)) {
            throw new IllegalArgumentException("命令 Topic 与当前设备不匹配");
        }

        final IrrigationCommand command;

        try {
            command = objectMapper.readValue(payload, IrrigationCommand.class);
        } catch (JacksonException exception) {
            throw new IllegalArgumentException(
                    "灌溉命令不是合法 JSON",
                    exception);
        }

        if (command == null) {
            throw new IllegalArgumentException(
                    "灌溉命令不能为空");
        }

        if (command.recordId() == null) {
            throw new IllegalArgumentException(
                    "recordId 不能为空");
        }

        if (command.action() == null
                || command.action().isBlank()) {
            throw new IllegalArgumentException(
                    "action 不能为空");
        }

        if (command.issuedAt() == null) {
            throw new IllegalArgumentException(
                    "issuedAt 不能为空");
        }

        Instant now = Instant.now();

        if (command.issuedAt()
                .isBefore(now.minusSeconds(300))) {
            throw new IllegalArgumentException(
                    "拒绝执行超过 5 分钟的旧命令");
        }

        switch (command.action()) {
            case "start" -> {
                if (command.plannedDuration() == null
                        || command.plannedDuration() < 1
                        || command.plannedDuration() > 180) {
                    throw new IllegalArgumentException(
                            "启动命令的 plannedDuration 必须在 1 到 180 之间");
                }
            }

            case "stop" -> {
                if (command.plannedDuration() != null) {
                    throw new IllegalArgumentException(
                            "停止命令不能包含 plannedDuration");
                }
            }

            default -> throw new IllegalArgumentException(
                    "action 只能是 start 或 stop");
        }

        return command;
    }

    private synchronized void startIrrigation (
        IrrigationCommand command
    ) {
        if (command.recordId().equals(activeRecordId)) {
            return;
        }

        if (activeRecordId != null) {
            publishAck(new IrrigationAck(command.recordId(), "start", "failed", Instant.now(), null, null,
                    "控制器正在执行其他灌溉任务"));
            return;
        }

        activeRecordId = command.recordId();
        irrigationStartedAt = Instant.now();

        publishAck(new IrrigationAck(command.recordId(), "start", "running", irrigationStartedAt, null, null, null));

        completionTask = scheduler.schedule(() -> completeIrrigation(command), command.plannedDuration(),
                TimeUnit.MINUTES);
    }

    private synchronized void completeIrrigation(
        IrrigationCommand command
    ) {
        if (!command.recordId().equals(activeRecordId)) {
            return;
        }

        int duration = command.plannedDuration();

        BigDecimal waterUsage = WATER_USAGE_PER_MINUTE.multiply(BigDecimal.valueOf(duration));

        activeRecordId = null;
        irrigationStartedAt = null;
        completionTask = null;

        publishAck(
                new IrrigationAck(command.recordId(), "start", "completed", Instant.now(), duration, waterUsage, null));
    }

    private synchronized void stopIrrigation(
        IrrigationCommand command
    ) {
        if (activeRecordId == null) {
            publishAck(new IrrigationAck(
                    command.recordId(),
                    "stop",
                    "failed",
                    Instant.now(),
                    null,
                    null,
                    "控制器当前没有正在执行的任务"));
            return;
        }
        if (!command.recordId().equals(activeRecordId)) {
            publishAck(new IrrigationAck(command.recordId(), "stop", "failed", Instant.now(), null, null,
                    "停止命令与当前灌溉任务不匹配"));
            return;
        }
        Instant endedAt = Instant.now();

        if (completionTask != null) {
            completionTask.cancel(false);
        }

        int duration = Math.toIntExact(
                Duration.between(irrigationStartedAt, endedAt).toMinutes());

        BigDecimal waterUsage = WATER_USAGE_PER_MINUTE.multiply(BigDecimal.valueOf(duration));

        activeRecordId = null;
        irrigationStartedAt = null;
        completionTask = null;

        publishAck(
                new IrrigationAck(command.recordId(), "stop", "completed", endedAt, duration, waterUsage, null));
    }

    private void publishAck(IrrigationAck ack) {
        final byte[] payload;
        try {
            payload = objectMapper.writeValueAsBytes(ack);
        } catch (JacksonException exception) {
            System.err.println("IrrigationAck 对象序列化失败，原因: " + exception.getMessage());
            return;
        }

        String topic = ACK_TOPIC_TEMPLATE.formatted(deviceId);

        if (client == null || !client.isConnected()) {
            System.err.println("当前设备客户端未初始化/未登录，无法发送灌溉回执, deviceId=%s".formatted(deviceId));

            return;
        }

        try {
            client.publish(topic, payload, 1, false);
        } catch (MqttException exception) {
            System.err.println("发布灌溉回执失败, deviceId=%s, reason=%s".formatted(deviceId, exception.getMessage()));
        }
    }
}
