package com.farmwise.monitoring.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.farmwise.monitoring.dto.SensorReadingResponse;
import com.farmwise.monitoring.service.MonitoringService;
import com.farmwise.security.permission.RequiredPermission;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sensor-readings")
@RequiredArgsConstructor
public class SensorReadingController {
    private final MonitoringService monitoringService;

    @GetMapping
    @RequiredPermission("environment:read")
    public ResponseEntity<List<SensorReadingResponse>> listSensorReadings(
            @RequestParam String landId,
            @RequestParam(required = false) String metric,
            @RequestParam(required = false)
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDateTime startAt,
            @RequestParam(required = false)
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDateTime endAt,
            Authentication authentication) {
        String userId = authentication.getName();
        List<SensorReadingResponse> response =
                monitoringService.listSensorReadings(
                        userId,
                        landId,
                        metric,
                        startAt,
                        endAt);

        return ResponseEntity.ok(response);
    }
}
