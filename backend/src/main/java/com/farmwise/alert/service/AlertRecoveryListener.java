package com.farmwise.alert.service;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.farmwise.task.event.FarmTaskStatusChangedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertRecoveryListener {
    private final AlertEvaluationService alertEvaluationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTaskStatusChanged(
        FarmTaskStatusChangedEvent event
    ) {
        if (!"alert".equals(event.sourceType())
                || event.sourceId() == null
                || (!"completed".equals(event.status())
                        && !"cancelled".equals(event.status()))) {
            return;
        }

        try {
            alertEvaluationService.retryRecovery(event.sourceId(), event.changedAt());
        } catch (RuntimeException exception) {
            log.error("任务结束后重试预警恢复失败，alertId={}", event.sourceId(), exception);
        }
    }
}
