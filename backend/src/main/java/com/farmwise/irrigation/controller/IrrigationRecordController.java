package com.farmwise.irrigation.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.farmwise.irrigation.dto.IrrigationRecordResponse;
import com.farmwise.irrigation.service.IrrigationService;
import com.farmwise.security.permission.RequiredPermission;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/irrigation-records")
@RequiredArgsConstructor
public class IrrigationRecordController {
    private final IrrigationService irrigationService;

    @GetMapping
    @RequiredPermission("irrigation:read")
    public ResponseEntity<List<IrrigationRecordResponse>> listIrrigationRecords(
            @RequestParam String landId,
            @RequestParam(required = false)
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDateTime startAt,
            @RequestParam(required = false)
                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                    LocalDateTime endAt,
            @RequestParam(required = false) String status,
            Authentication authentication) {
        List<IrrigationRecordResponse> response = irrigationService.listIrrigationRecords(
                authentication.getName(),
                landId,
                startAt,
                endAt,
                status);

        return ResponseEntity.ok(response);
    }
}
