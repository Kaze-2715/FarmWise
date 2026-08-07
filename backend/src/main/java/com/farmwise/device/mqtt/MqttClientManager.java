package com.farmwise.device.mqtt;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.farmwise.irrigation.mqtt.IrrigationCommand;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class MqttClientManager implements MqttCallbackExtended {
    private static final long MQTT_OPERATION_TIMEOUT_MILLISECONDS = 15_000;
    private static final int MAX_SUBSCRIPTION_ATTEMPTS = 3;

    private final MqttService mqttService;
    private final MqttProperties properties;
    private MqttClient client;

    private final ObjectMapper objectMapper;

    private final ExecutorService subscriptionExecutor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "mqtt-subscription");
        thread.setDaemon(true);
        return thread;
    });

    @Override
    public void messageArrived(String topic, MqttMessage message) {
        try {
            if (topic.endsWith("/telemetry")) {
                mqttService.processTelemetry(topic, message.getPayload());
                return;
            } else if (topic.endsWith("/status")) {
                mqttService.processStatus(topic, message.getPayload());
                return;
            } else if (topic.endsWith("/command-acks")) {
                mqttService.processAck(topic, message.getPayload());
                return;
            } else {
                throw new IllegalArgumentException("不支持的 MQTT Topic");
            }
        } catch (IllegalArgumentException exception) {
            log.warn("丢弃非法 MQTT 消息, topic={}, reason={}", topic, exception.getMessage());
        }
    }

    public void publishCommand(String deviceId, IrrigationCommand command) {
        if (client == null || !client.isConnected()) {
            throw new IllegalStateException("MQTT 客户端未初始化或未连接");
        }
        final byte[] payload;
        try {
            payload = objectMapper.writeValueAsBytes(command);
        } catch (JacksonException exception) {
            throw new IllegalStateException("命令序列化失败", exception);
        }

        String topic = properties.commandTopic().formatted(deviceId);

        try {
            client.publish(topic, payload, 1, false);
        } catch (MqttException exception) {
            throw new IllegalStateException("MQTT 命令发布失败", exception);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {}

    @Override
    public void connectComplete(boolean reconnect, String serverUri) {
        if (!StringUtils.hasText(properties.telemetryTopic())) {
            throw new IllegalStateException("MQTT telemetry topic 不能为空");
        }
        if (!StringUtils.hasText(properties.statusTopic())) {
            throw new IllegalStateException("MQTT status topic 不能为空");
        }
        if (!StringUtils.hasText(properties.commandAckTopic())) {
            throw new IllegalStateException("MQTT commandAck topic 不能为空");
        }

        if (properties.qos() < 0 || properties.qos() > 2) {
            throw new IllegalStateException("MQTT QoS 必须是 0、1 或 2");
        }

        String[] topics = {
                properties.telemetryTopic(),
                properties.statusTopic(),
                properties.commandAckTopic()};

        int[] qos = {properties.qos(), properties.qos(), properties.qos()};

        subscriptionExecutor.execute(() -> subscribe(topics, qos, serverUri, reconnect));
    }

    public boolean isAvailable() {
        return client != null && client.isConnected();
    }

    @EventListener(ApplicationReadyEvent.class)
    void start() throws MqttException {
        if (!properties.enabled()) {
            log.info("MQTT 功能未启用");
            return;
        }
        client = new MqttClient(properties.brokerUri(), properties.clientId());
        client.setTimeToWait(MQTT_OPERATION_TIMEOUT_MILLISECONDS);

        client.setCallback(this);
        client.connect(createConnectOptions());
    }

    @PreDestroy
    void stop() {
        subscriptionExecutor.shutdownNow();

        if (client == null) {
            return;
        }
        try {
            if (client.isConnected()) {
                client.disconnect(5_000);
            }
        } catch (MqttException exception) {
            log.warn("断开 MQTT 连接失败", exception);
        }

        try {
            client.close();
        } catch (MqttException exception) {
            log.warn("关闭 MQTT 客户端失败", exception);
        }

        log.info("MQTT 客户端已关闭");
    }

    private MqttConnectOptions createConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();

        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(properties.connectionTimeoutSeconds());
        options.setKeepAliveInterval(properties.keepAliveSeconds());

        if (StringUtils.hasText(properties.username())) {
            options.setUserName(properties.username());
            options.setPassword(properties.password().toCharArray());
        }

        return options;
    }

    private void subscribe(String[] topics, int[] qos, String serverUri, boolean reconnect) {
        for (int attempt = 1; attempt <= MAX_SUBSCRIPTION_ATTEMPTS; attempt++) {
            if (!client.isConnected()) {
                return;
            }

            try {
                client.subscribe(topics, qos);
                log.info(
                        "MQTT 连接并订阅完成，broker={}, topics={}, qos={}, reconnect={}",
                        serverUri,
                        String.join(", ", topics),
                        properties.qos(),
                        reconnect);
                return;
            } catch (MqttException exception) {
                if (attempt == MAX_SUBSCRIPTION_ATTEMPTS) {
                    log.error("订阅 MQTT Topic 失败，已达到最大重试次数", exception);
                    return;
                }
                log.warn("订阅 MQTT Topic 失败，即将重试，attempt={}", attempt, exception);
            }
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.warn("MQTT 连接断开", cause);
    }
}
