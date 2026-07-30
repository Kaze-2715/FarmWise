package com.farmwise.task.controller;

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

import com.farmwise.security.permission.RequiredPermission;
import com.farmwise.task.dto.CancelFarmTaskRequest;
import com.farmwise.task.dto.CompleteFarmTaskRequest;
import com.farmwise.task.dto.CreateFarmTaskRequest;
import com.farmwise.task.dto.FarmTaskResponse;
import com.farmwise.task.service.FarmTaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/farm-tasks")
@RequiredArgsConstructor
public class FarmTaskController {
    private final FarmTaskService farmTaskService;

    @GetMapping
    @RequiredPermission("farm_task:read")
    public ResponseEntity<List<FarmTaskResponse>> listFarmTasks(
            @RequestParam String landId,
            @RequestParam(required = false) String taskType,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String status,
            Authentication authentication) {
        String userId = authentication.getName();
        List<FarmTaskResponse> response = farmTaskService.listFarmTasks(
                userId,
                landId,
                taskType,
                priority,
                status);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @RequiredPermission("farm_task:manage")
    public ResponseEntity<FarmTaskResponse> createManualTask(
            @Valid @RequestBody CreateFarmTaskRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        FarmTaskResponse response = farmTaskService.createFarmTask(userId, request, "manual", null);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{taskId}/start")
    @RequiredPermission("farm_task:manage")
    public ResponseEntity<FarmTaskResponse> startFarmTask(
            @PathVariable String taskId,
            Authentication authentication) {
        String userId = authentication.getName();
        FarmTaskResponse response = farmTaskService.startFarmTask(userId, taskId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{taskId}/complete")
    @RequiredPermission("farm_task:manage")
    public ResponseEntity<FarmTaskResponse> completeFarmTask(
            @PathVariable String taskId,
            @Valid @RequestBody CompleteFarmTaskRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        FarmTaskResponse response =
                farmTaskService.completeFarmTask(userId, taskId, request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{taskId}/cancel")
    @RequiredPermission("farm_task:manage")
    public ResponseEntity<FarmTaskResponse> cancelFarmTask(
            @PathVariable String taskId,
            @Valid @RequestBody CancelFarmTaskRequest request,
            Authentication authentication) {
        String userId = authentication.getName();
        FarmTaskResponse response =
                farmTaskService.cancelFarmTask(userId, taskId, request);

        return ResponseEntity.ok(response);
    }
}
