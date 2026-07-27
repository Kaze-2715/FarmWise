package com.farmwise.device.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.farmwise.device.dto.CreateDeviceRequest;
import com.farmwise.device.dto.DeviceResponse;
import com.farmwise.device.dto.UpdateDeviceRequest;
import com.farmwise.device.service.DeviceService;
import com.farmwise.security.permission.RequiredPermission;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceService deviceService;

    @GetMapping
    @RequiredPermission("device:read")
    public ResponseEntity<List<DeviceResponse>> listDevices(
            @RequestParam(required = false) String landId,
            @RequestParam(required = false) String deviceType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            Authentication authentication) {
        String userId = authentication.getName();

        List<DeviceResponse> response =
                deviceService.listDevices(userId, landId, deviceType, status, keyword);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @RequiredPermission("device:create")
    public ResponseEntity<DeviceResponse> createDevice(
            @Valid @RequestBody CreateDeviceRequest request, Authentication authentication) {
        String userId = authentication.getName();
        DeviceResponse response = deviceService.createDevice(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{deviceId}")
    @RequiredPermission("device:update")
    public ResponseEntity<DeviceResponse> updateDevice(
            @PathVariable String deviceId,
            @Valid @RequestBody UpdateDeviceRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        DeviceResponse response = deviceService.updateDevice(userId, deviceId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{deviceId}")
    @RequiredPermission("device:delete")
    public ResponseEntity<Void> deleteDevice(
            @PathVariable String deviceId, Authentication authentication) {
        String userId = authentication.getName();
        deviceService.deleteDevice(userId, deviceId);

        return ResponseEntity.noContent().build();
    }
}
