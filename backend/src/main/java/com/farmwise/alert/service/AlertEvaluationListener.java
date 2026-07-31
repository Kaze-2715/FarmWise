package com.farmwise.alert.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.farmwise.device.event.SensorReadingsSavedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertEvaluationListener {
    private final AlertEvaluationService alertEvaluationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleSensorReadingsSaved(
        SensorReadingsSavedEvent event
    ) {
        try {
            alertEvaluationService.evaluate(event);
        } catch (RuntimeException exception) {
            log.error("监测数据预警判定失败，messageId={}", event.messageId(), exception);
        }
    }
}
