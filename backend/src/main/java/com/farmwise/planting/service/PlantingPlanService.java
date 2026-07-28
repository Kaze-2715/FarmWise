package com.farmwise.planting.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmwise.common.exception.BizException;
import com.farmwise.land.mapper.LandMapper;
import com.farmwise.land.model.Land;
import com.farmwise.planting.dto.CreatePlantingPlanRequest;
import com.farmwise.planting.dto.PlantingPlanResponse;
import com.farmwise.planting.dto.UpdatePlantingPlanRequest;
import com.farmwise.planting.dto.UpdatePlantingPlanStatusRequest;
import com.farmwise.planting.mapper.PlantingPlanMapper;
import com.farmwise.planting.model.PlantingPlan;
import com.farmwise.task.mapper.FarmTaskMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlantingPlanService {

    private final LandMapper landMapper;
    private final PlantingPlanMapper plantingPlanMapper;
    private final FarmTaskMapper farmTaskMapper;

    @Transactional(readOnly = true)
    public List<PlantingPlanResponse> listPlantingPlans(String userId, String landId) {
        String normalizedLandId = normalizeRequired(landId, "地块 ID 不能为空");
        findOwnedLand(userId, normalizedLandId);

        return plantingPlanMapper.findAllByLandId(normalizedLandId)
                .stream()
                .map(PlantingPlanResponse::from)
                .toList();
    }

    @Transactional
    public PlantingPlanResponse createPlantingPlan(
            String userId,
            CreatePlantingPlanRequest request) {
        String landId = normalizeRequired(request.landId(), "地块 ID 不能为空");
        Land land = findOwnedLand(userId, landId);
        String planName = normalizeRequired(request.planName(), "计划名称不能为空");
        String cropType = normalizeRequired(request.cropType(), "作物类型不能为空");
        String remark = request.remark() == null ? "" : request.remark();
        validatePlanRules(
                request.area(),
                land.area(),
                request.plantingDate(),
                request.expectedHarvestDate());

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        PlantingPlan plan = new PlantingPlan(
                UUID.randomUUID().toString(),
                land.id(),
                planName,
                cropType,
                request.area(),
                request.plantingDate(),
                request.expectedHarvestDate(),
                "planned",
                remark,
                now,
                now);

        int affectedRows = plantingPlanMapper.add(plan);

        if (affectedRows != 1) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "新增种植计划失败");
        }

        return PlantingPlanResponse.from(plan);
    }

    @Transactional
    public PlantingPlanResponse updatePlantingPlan(
            String userId,
            String planId,
            UpdatePlantingPlanRequest request) {
        String normalizedPlanId = normalizeRequired(planId, "种植计划 ID 不能为空");
        PlantingPlan oldPlan = plantingPlanMapper.findByIdAndOwnerId(normalizedPlanId, userId)
                .orElseThrow(() -> new BizException(
                        HttpStatus.NOT_FOUND,
                        "种植计划不存在或不属于当前用户"));

        String landId = normalizeRequired(request.landId(), "地块 ID 不能为空");
        Land land = findOwnedLand(userId, landId);
        String planName = normalizeRequired(request.planName(), "计划名称不能为空");
        String cropType = normalizeRequired(request.cropType(), "作物类型不能为空");
        String remark = request.remark() == null ? "" : request.remark();
        validatePlanRules(
                request.area(),
                land.area(),
                request.plantingDate(),
                request.expectedHarvestDate());

        PlantingPlan updatedPlan = new PlantingPlan(
                oldPlan.id(),
                land.id(),
                planName,
                cropType,
                request.area(),
                request.plantingDate(),
                request.expectedHarvestDate(),
                oldPlan.status(),
                remark,
                oldPlan.createdAt(),
                LocalDateTime.now(ZoneOffset.UTC));

        int affectedRows = plantingPlanMapper.update(updatedPlan);
        if (affectedRows != 1) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "更新种植计划失败");
        }

        return PlantingPlanResponse.from(updatedPlan);
    }

    @Transactional
    public void deletePlantingPlan(String userId, String planId) {
        planId = normalizeRequired(planId, "种植计划 ID 不能为空");
        plantingPlanMapper.findByIdAndOwnerIdForUpdate(planId, userId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "计划不存在或不属于当前用户"));
        boolean existsTask = farmTaskMapper.existsBySourceTypeAndSourceId("plan", planId);

        if (existsTask) {
            throw new BizException(HttpStatus.CONFLICT, "存在关联的农事任务");
        }

        int affectedRows = plantingPlanMapper.delete(planId);

        if (affectedRows != 1) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "删除种植计划失败");
        }
    }

    @Transactional
    public PlantingPlanResponse updatePlantingPlanStatus(
            String userId,
            String planId,
            UpdatePlantingPlanStatusRequest request) {
        planId = normalizeRequired(planId, "种植计划 ID 不能为空");
        PlantingPlan plan = plantingPlanMapper.findByIdAndOwnerIdForUpdate(planId, userId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "计划不存在或不属于当前用户"));

        String currentStatus = plan.status();
        String targetStatus = normalizeRequired(request.status(), "目标状态不能为空");

        if (currentStatus.equals(targetStatus)) {
            return PlantingPlanResponse.from(plan);
        }

        validateStatusChange(currentStatus, targetStatus);

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        PlantingPlan newPlan = new PlantingPlan(plan.id(), plan.landId(), plan.planName(), plan.cropType(), plan.area(),
                plan.plantingDate(), plan.expectedHarvestDate(), targetStatus, plan.remark(), plan.createdAt(), now);

        int affectedRows = plantingPlanMapper.updateStatus(planId, targetStatus, now);

        if (affectedRows != 1) {
            throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "更新种植计划状态失败");
        }

        return PlantingPlanResponse.from(newPlan);
    }

    private void validateStatusChange(String currentStatus, String targetStatus) {
        switch (targetStatus) {
            case "sowing" -> {
                if ("planned".equals(currentStatus)) {
                    return;
                }
                throw new BizException(HttpStatus.BAD_REQUEST, "sowing 的前置状态只能是 planned");
            }
            case "growing" -> {
                if ("sowing".equals(currentStatus)) {
                    return;
                }
                throw new BizException(HttpStatus.BAD_REQUEST, "growing 的前置状态只能是 sowing");
            }
            case "harvested" -> {
                if ("growing".equals(currentStatus)) {
                    return;
                }
                throw new BizException(HttpStatus.BAD_REQUEST, "harvested 的前置状态只能是 growing");
            }
            case "cancelled" -> {
                if ("harvested".equals(currentStatus)) {
                    throw new BizException(HttpStatus.BAD_REQUEST, "作物已经收获，不能取消计划");
                }
                return;
            }
            default -> {
                throw new BizException(HttpStatus.BAD_REQUEST, "不支持的种植计划状态: " + targetStatus);
            }
        }
    }

    private String normalizeRequired(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new BizException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        return value.strip();
    }

    private Land findOwnedLand(String userId, String landId) {
        return landMapper.findByIdAndOwnerId(landId, userId)
                .orElseThrow(() -> new BizException(
                        HttpStatus.NOT_FOUND,
                        "地块不存在或不属于当前用户"));
    }

    private void validatePlanRules(
            BigDecimal area,
            BigDecimal landArea,
            LocalDate plantingDate,
            LocalDate expectedHarvestDate) {
        if (area.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException(HttpStatus.BAD_REQUEST, "种植面积必须大于 0");
        }
        if (area.compareTo(landArea) > 0) {
            throw new BizException(HttpStatus.BAD_REQUEST, "种植面积不能大于地块面积");
        }
        if (expectedHarvestDate.isBefore(plantingDate)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "预计收获日期不能早于种植日期");
        }
    }
}
