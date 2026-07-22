package com.farmwise.land.controller;

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

import com.farmwise.land.dto.CreateLandRequest;
import com.farmwise.land.dto.LandResponse;
import com.farmwise.land.dto.UpdateLandRequest;
import com.farmwise.land.service.LandService;
import com.farmwise.security.permission.RequiredPermission;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lands")
@RequiredArgsConstructor
public class LandController {
    private final LandService landService;

    @GetMapping
    @RequiredPermission("land:read")
    public ResponseEntity<List<LandResponse>> listLands(
            Authentication authentication) {
        String userId = authentication.getName();
        List<LandResponse> lands = landService.listCurrentUserLands(userId);
        return ResponseEntity.ok(lands);
    }

    @PostMapping
    @RequiredPermission("land:create")
    public ResponseEntity<LandResponse> createLand(
            @Valid @RequestBody CreateLandRequest request,
            Authentication authentication) {
        String userId = authentication.getName();

        LandResponse response = landService.createLand(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{landId}")
    @RequiredPermission("land:update")
    public ResponseEntity<LandResponse> updateLand(
            @Valid @RequestBody UpdateLandRequest request,
            @PathVariable String landId,
            Authentication authentication) {
        String userId = authentication.getName();

        LandResponse response = landService.updateLand(userId, landId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{landId}")
    @RequiredPermission("land:delete")
    public ResponseEntity<Void> deleteLand(
            @PathVariable String landId,
            Authentication authentication) {
        String userId = authentication.getName();

        landService.deleteLand(landId, userId);

        return ResponseEntity.noContent().build();
    }
}
