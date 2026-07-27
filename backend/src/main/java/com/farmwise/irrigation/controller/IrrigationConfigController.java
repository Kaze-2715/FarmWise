package com.farmwise.irrigation.controller;

import java.util.List;

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

import com.farmwise.irrigation.dto.IrrigationConfigResponse;
import com.farmwise.irrigation.dto.SaveIrrigationConfigRequest;
import com.farmwise.irrigation.service.IrrigationService;
import com.farmwise.security.permission.RequiredPermission;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lands/{landId}/irrigation-configs")
@RequiredArgsConstructor
public class IrrigationConfigController {
    private final IrrigationService irrigationService;

    @PostMapping
    @RequiredPermission("irrigation:configure")
    public ResponseEntity<IrrigationConfigResponse> createConfig(
            @PathVariable String landId,
            @Valid @RequestBody SaveIrrigationConfigRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        IrrigationConfigResponse response = irrigationService.createConfig(userId, landId, request);

        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{configId}")
    @RequiredPermission("irrigation:configure")
    public ResponseEntity<IrrigationConfigResponse> updateConfig(
            @PathVariable String landId,
            @PathVariable String configId,
            @Valid @RequestBody SaveIrrigationConfigRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        IrrigationConfigResponse response =
                irrigationService.updateConfig(userId, landId, configId, request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{configId}/enable")
    @RequiredPermission("irrigation:configure")
    public ResponseEntity<Void> enableConfig(
            @PathVariable String landId,
            @PathVariable String configId,
            Authentication authentication) {
        String userId = authentication.getName();

        irrigationService.enableConfig(userId, landId, configId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{configId}")
    @RequiredPermission("irrigation:configure")
    public ResponseEntity<Void> deleteConfig(
            @PathVariable String landId,
            @PathVariable String configId,
            Authentication authentication) {
        String userId = authentication.getName();
        irrigationService.deleteConfig(userId, landId, configId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @RequiredPermission("irrigation:read")
    public ResponseEntity<List<IrrigationConfigResponse>> listConfigs(
            @PathVariable String landId, Authentication authentication) {
        String userId = authentication.getName();

        List<IrrigationConfigResponse> response = irrigationService.listConfigs(userId, landId);

        return ResponseEntity.ok(response);
    }
}
