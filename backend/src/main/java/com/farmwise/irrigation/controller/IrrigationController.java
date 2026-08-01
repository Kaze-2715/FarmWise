package com.farmwise.irrigation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.farmwise.irrigation.dto.IrrigationBatchResponse;
import com.farmwise.irrigation.dto.StartIrrigationRequest;
import com.farmwise.irrigation.service.IrrigationService;
import com.farmwise.security.permission.RequiredPermission;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/irrigations")
@RequiredArgsConstructor
public class IrrigationController {
    private final IrrigationService irrigationService;

    @PostMapping
    @RequiredPermission("device:control")
    public ResponseEntity<IrrigationBatchResponse> startIrrigation(
            @Valid @RequestBody StartIrrigationRequest request, Authentication authentication) {
        String userId = authentication.getName();
        IrrigationBatchResponse response = irrigationService.startIrrigation(userId, request);

        return ResponseEntity.accepted().body(response);
    }

    @PostMapping("/{recordId}/stop")
    @RequiredPermission("device:control")
    public ResponseEntity<Void> stopIrrigation(
            @PathVariable String recordId, Authentication authentication) {
        String userId = authentication.getName();

        irrigationService.stopIrrigation(userId, recordId);

        return ResponseEntity.accepted().build();
    }
}
