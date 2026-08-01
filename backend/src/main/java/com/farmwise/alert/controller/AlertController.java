package com.farmwise.alert.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.farmwise.alert.dto.AlertResponse;
import com.farmwise.alert.dto.CreateAlertRequest;
import com.farmwise.alert.dto.IgnoreAlertRequest;
import com.farmwise.alert.dto.ResolveAlertRequest;
import com.farmwise.alert.dto.StartAlertRequest;
import com.farmwise.alert.dto.StartAlertResponse;
import com.farmwise.alert.service.AlertService;
import com.farmwise.security.permission.RequiredPermission;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {
    private final AlertService alertService;

    @GetMapping
    @RequiredPermission("alert:read")
    public ResponseEntity<List<AlertResponse>> listAlerts(
            @RequestParam String landId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String status,
            Authentication authentication) {
        String userId = authentication.getName();
        List<AlertResponse> response = alertService.listAlerts(
                userId,
                landId,
                type,
                severity,
                status);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @RequiredPermission("alert:manage")
    public ResponseEntity<AlertResponse> createAlert(
            @Valid @RequestBody CreateAlertRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        AlertResponse response = alertService.createAlert(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{alertId}/start")
    @RequiredPermission("alert:manage")
    public ResponseEntity<StartAlertResponse> startAlert(
            @PathVariable String alertId,
            @RequestBody StartAlertRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        StartAlertResponse response = alertService.startAlert(userId, alertId, request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{alertId}/resolve")
    @RequiredPermission("alert:manage")
    public ResponseEntity<Void> resolveAlert(
            @PathVariable String alertId,
            @Valid @RequestBody ResolveAlertRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        alertService.resolveAlert(userId, alertId, request);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{alertId}/ignore")
    @RequiredPermission("alert:manage")
    public ResponseEntity<Void> ignoreAlert(
            @PathVariable String alertId,
            @Valid @RequestBody IgnoreAlertRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        alertService.ignoreAlert(userId, alertId, request);

        return ResponseEntity.noContent().build();
    }
}
