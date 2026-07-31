package com.farmwise.land.service;

import static com.farmwise.common.util.ValidationUtil.validateOptional;
import static com.farmwise.common.util.ValidationUtil.validateRequired;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.farmwise.common.exception.BizException;
import com.farmwise.land.dto.CreateLandRequest;
import com.farmwise.land.dto.LandResponse;
import com.farmwise.land.dto.UpdateLandRequest;
import com.farmwise.land.mapper.LandMapper;
import com.farmwise.land.model.Land;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LandService {
    private final LandMapper landMapper;

    private static final Set<String> ALLOWED_LAND_TYPES = Set.of(
            "paddy", "dryland", "greenhouse");

    private static final Set<String> ALLOWED_STATUS = Set.of(
            "inactive", "cultivating", "fallow", "abnormal");

    @Transactional(readOnly = true)
    public List<LandResponse> listCurrentUserLands(String ownerId) {
        return landMapper.findAllByOwnerId(ownerId)
                .stream()
                .map(LandResponse::from)
                .toList();
    }

    @Transactional
    public LandResponse createLand(String ownerId, CreateLandRequest request) {
        String landId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        String name = validateRequired(request.name(), "地块名称不能为空");
        String landType = validateRequired(request.landType(), "地块类型不能为空");
        if (!ALLOWED_LAND_TYPES.contains(landType)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "不支持的土壤类型");
        }
        String crop = validateOptional(request.crop());
        String status = validateRequired(request.status(), "地块状态不能为空");
        if (!ALLOWED_STATUS.contains(status)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "不支持的地块状态");
        }
        String location = validateRequired(request.location(), "地块位置不能为空");

        Land land = new Land(
                landId,
                ownerId,
                name,
                landType,
                request.area(),
                crop,
                status,
                location,
                request.longitude(),
                request.latitude(),
                now,
                now);

        try {
            landMapper.addLand(land);
        } catch (DuplicateKeyException exception) {
            throw new BizException(HttpStatus.CONFLICT, "地块名称已存在");
        }

        return LandResponse.from(land);
    }

    @Transactional
    public LandResponse updateLand(
            String ownerId,
            String landId,
            UpdateLandRequest request) {
        Land oldLand = landMapper.findByIdAndOwnerId(landId, ownerId)
                .orElseThrow(() -> new BizException(HttpStatus.NOT_FOUND, "请求更新的地块不存在"));

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        String name = validateRequired(request.name(), "地块名称不能为空");
        String landType = validateRequired(request.landType(), "地块类型不能为空");
        if (!ALLOWED_LAND_TYPES.contains(landType)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "不支持的土壤类型");
        }
        String crop = validateOptional(request.crop());
        String status = validateRequired(request.status(), "地块状态不能为空");
        if (!ALLOWED_STATUS.contains(status)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "不支持的地块状态");
        }
        String location = validateRequired(request.location(), "地块位置不能为空");

        Land newLand = new Land(
                oldLand.id(),
                oldLand.ownerId(),
                name,
                landType,
                request.area(),
                crop,
                status,
                location,
                request.longitude(),
                request.latitude(),
                oldLand.createdAt(),
                now);

        try {
            int updatedRows = landMapper.updateLand(newLand);

            if (updatedRows == 0) {
                throw new BizException(HttpStatus.NOT_FOUND, "更新地块失败");
            }
        } catch (DuplicateKeyException exception) {
            throw new BizException(HttpStatus.CONFLICT, "地块名称已经存在");
        }

        return LandResponse.from(newLand);
    }

    @Transactional
    public void deleteLand(String landId, String ownerId) {
        try {
            int deletedRows = landMapper.deleteByIdAndOwnerId(landId, ownerId);

            if (deletedRows == 0) {
                throw new BizException(HttpStatus.NOT_FOUND, "地块不存在");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new BizException(HttpStatus.CONFLICT, "地块存在关联业务数据，无法删除");
        }
    }

}
