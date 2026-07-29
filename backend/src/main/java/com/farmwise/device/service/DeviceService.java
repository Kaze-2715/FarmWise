package com.farmwise.device.service;

import java.time.LocalDate;
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
import com.farmwise.device.dto.CreateDeviceRequest;
import com.farmwise.device.dto.DeviceResponse;
import com.farmwise.device.dto.UpdateDeviceRequest;
import com.farmwise.device.mapper.DeviceMapper;
import com.farmwise.device.model.Device;
import com.farmwise.land.mapper.LandMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private static final Set<String> ALLOWED_DEVICE_STATUS = Set.of("online", "offline");
    private static final Set<String> ALLOWED_DEVICE_TYPE =
            Set.of("soil_moisture_sensor",
                   "air_temp_humidity_sensor",
                   "light_sensor",
                   "soil_ph_sensor",
                   "pest_camera",
                   "irrigation_controller");

    private final DeviceMapper deviceMapper;
    private final LandMapper landMapper;

    @Transactional(readOnly = true)
    public List<DeviceResponse> listDevices(
            String userId, String landId, String deviceType, String status, String keyword) {
        landId = validate(landId);
        deviceType = validate(deviceType);
        status = validate(status);
        keyword = validate(keyword);

        if (deviceType != null && !ALLOWED_DEVICE_TYPE.contains(deviceType)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "不支持的设备类型");
        }

        if (status != null && !ALLOWED_DEVICE_STATUS.contains(status)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "不支持的设备状态");
        }

        List<Device> devices =
                deviceMapper.findAllByOwnerId(userId, landId, deviceType, status, keyword);

        return devices.stream().map(DeviceResponse::from).toList();
    }

    @Transactional
    public DeviceResponse createDevice(String userId, CreateDeviceRequest request) {
        String name = validate(request.name());
        String model = validate(request.model());
        String type = validate(request.deviceType());
        if (!ALLOWED_DEVICE_TYPE.contains(type)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "不支持的设备类型");
        }
        String landId = validate(request.landId());
        if (landId != null) {
            landMapper.findByIdAndOwnerId(landId, userId)
                    .orElseThrow(
                            () -> new BizException(HttpStatus.BAD_REQUEST, "地块不属于该用户"));
        }
        String id = UUID.randomUUID().toString();
        LocalDate installDate = request.installDate();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        Device device = new Device(
                id,
                userId,
                landId,
                name,
                type,
                "offline",
                null,
                null,
                model,
                installDate,
                request.longitude(),
                request.latitude(),
                now,
                now);
        try {
            int addedRows = deviceMapper.addDevice(device);
            if (addedRows == 0) {
                throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "添加设备失败");
            }
        } catch (DuplicateKeyException exception) {
            throw new BizException(HttpStatus.CONFLICT, "设备名称已存在");
        }
        return DeviceResponse.from(device);
    }

    @Transactional
    public DeviceResponse updateDevice(
            String userId, String deviceId, UpdateDeviceRequest request) {
        Device oldDevice =
                deviceMapper.findByIdAndOwnerId(deviceId, userId)
                        .orElseThrow(
                                () -> new BizException(HttpStatus.NOT_FOUND, "要修改的设备不存在"));
        String name = validate(request.name());
        String model = validate(request.model());
        String deviceType = validate(request.deviceType());
        String landId = validate(request.landId());
        if (!ALLOWED_DEVICE_TYPE.contains(deviceType)) {
            throw new BizException(HttpStatus.BAD_REQUEST, "不支持的设备类型");
        }
        if (landId != null) {
            landMapper.findByIdAndOwnerId(landId, userId)
                    .orElseThrow(
                            ()
                                    -> new BizException(
                                            HttpStatus.BAD_REQUEST,
                                            "要修改的设备所属的地块不存在"));
        }
        Device newDevice = new Device(
                deviceId,
                userId,
                landId,
                name,
                deviceType,
                oldDevice.status(),
                oldDevice.battery(),
                oldDevice.lastReportedAt(),
                model,
                request.installDate(),
                request.longitude(),
                request.latitude(),
                oldDevice.createdAt(),
                LocalDateTime.now(ZoneOffset.UTC));

        try {
            int updatedRows = deviceMapper.updateDevice(newDevice);
            if (updatedRows != 1) {
                throw new BizException(HttpStatus.INTERNAL_SERVER_ERROR, "修改设备信息失败");
            }
        } catch (DuplicateKeyException exception) {
            throw new BizException(HttpStatus.CONFLICT, "设备名称已经存在");
        }

        return DeviceResponse.from(newDevice);
    }

    @Transactional
    public void deleteDevice(String userId, String deviceId) {
        try {
            int deletedRows = deviceMapper.deleteByIdAndOwnerId(deviceId, userId);

            if (deletedRows == 0) {
                throw new BizException(HttpStatus.NOT_FOUND, "设备不存在");
            }
        } catch (DataIntegrityViolationException exception) {
            throw new BizException(HttpStatus.CONFLICT, "设备存在关联业务数据，无法删除");
        }
    }

    private String validate(String value) {
        if (value == null) {
            return null;
        }
        value = value.strip();
        if (value.isBlank()) {
            return null;
        }
        return value;
    }
}
