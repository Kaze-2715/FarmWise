package com.farmwise.device.mqtt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "farmwise.mqtt")
public record MqttProperties(
        boolean enabled,
        String brokerUri,
        String clientId,
        String telemetryTopic,
        String statusTopic,
        String commandTopic,
        String commandAckTopic,
        int qos,
        int connectionTimeoutSeconds,
        int offlineTimeoutSeconds,
        int keepAliveSeconds,
        String username,
        String password) {}
