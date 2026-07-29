package com.farmwise.monitoring.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farmwise.monitoring.dto.CreateEnvironmentThresholdRequest;
import com.farmwise.monitoring.dto.EnvironmentThresholdResponse;
import com.farmwise.monitoring.dto.UpdateEnvironmentThresholdRequest;
import com.farmwise.monitoring.service.MonitoringService;
import com.farmwise.security.permission.RequiredPermission;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lands/{landId}/environment-thresholds")
@RequiredArgsConstructor
public class EnvironmentThresholdController {
    private final MonitoringService monitoringService;

    @GetMapping
    @RequiredPermission("environment:read")
    public ResponseEntity<List<EnvironmentThresholdResponse>> listEnvironmentThresholds(
            @PathVariable String landId,
            Authentication authentication) {
        String userId = authentication.getName();
        List<EnvironmentThresholdResponse> response =
                monitoringService.listEnvironmentThresholds(userId, landId);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @RequiredPermission("environment_threshold:manage")
    public ResponseEntity<EnvironmentThresholdResponse> createEnvironmentThreshold(
            @PathVariable String landId,
            @Valid @RequestBody CreateEnvironmentThresholdRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        EnvironmentThresholdResponse response =
                monitoringService.createEnvironmentThreshold(userId, landId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{metric}")
    @RequiredPermission("environment_threshold:manage")
    public ResponseEntity<EnvironmentThresholdResponse> updateEnvironmentThreshold(
            @PathVariable String landId,
            @PathVariable String metric,
            @Valid @RequestBody UpdateEnvironmentThresholdRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        EnvironmentThresholdResponse response =
                monitoringService.updateEnvironmentThreshold(
                        userId,
                        landId,
                        metric,
                        request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{metric}")
    @RequiredPermission("environment_threshold:manage")
    public ResponseEntity<Void> deleteEnvironmentThreshold(
            @PathVariable String landId,
            @PathVariable String metric,
            Authentication authentication) {
        String userId = authentication.getName();
        monitoringService.deleteEnvironmentThreshold(userId, landId, metric);

        return ResponseEntity.noContent().build();
    }
}
