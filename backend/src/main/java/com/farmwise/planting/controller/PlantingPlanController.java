package com.farmwise.planting.controller;

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

import com.farmwise.planting.dto.CreatePlantingPlanRequest;
import com.farmwise.planting.dto.PlantingPlanResponse;
import com.farmwise.planting.dto.UpdatePlantingPlanRequest;
import com.farmwise.planting.dto.UpdatePlantingPlanStatusRequest;
import com.farmwise.planting.service.PlantingPlanService;
import com.farmwise.security.permission.RequiredPermission;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/planting-plans")
@RequiredArgsConstructor
public class PlantingPlanController {
    private final PlantingPlanService plantingPlanService;

    @GetMapping
    @RequiredPermission("planting_plan:read")
    public ResponseEntity<List<PlantingPlanResponse>> listPlantingPlans(
            @RequestParam String landId,
            Authentication authentication) {
        String userId = authentication.getName();
        List<PlantingPlanResponse> response =
                plantingPlanService.listPlantingPlans(userId, landId);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @RequiredPermission("planting_plan:manage")
    public ResponseEntity<PlantingPlanResponse> createPlantingPlan(
            @Valid @RequestBody CreatePlantingPlanRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        PlantingPlanResponse response =
                plantingPlanService.createPlantingPlan(userId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{planId}")
    @RequiredPermission("planting_plan:manage")
    public ResponseEntity<PlantingPlanResponse> updatePlantingPlan(
            @PathVariable String planId,
            @Valid @RequestBody UpdatePlantingPlanRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        PlantingPlanResponse response =
                plantingPlanService.updatePlantingPlan(userId, planId, request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{planId}")
    @RequiredPermission("planting_plan:manage")
    public ResponseEntity<Void> deletePlantingPlan(
            @PathVariable String planId,
            Authentication authentication) {
        String userId = authentication.getName();
        plantingPlanService.deletePlantingPlan(userId, planId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{planId}/status")
    @RequiredPermission("planting_plan:manage")
    public ResponseEntity<PlantingPlanResponse> updatePlantingPlanStatus(
            @PathVariable String planId,
            @Valid @RequestBody UpdatePlantingPlanStatusRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        PlantingPlanResponse response =
                plantingPlanService.updatePlantingPlanStatus(userId, planId, request);

        return ResponseEntity.ok(response);
    }
}
