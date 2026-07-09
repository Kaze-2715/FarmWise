import { ref } from "vue";
import { mockLands, mockDevices } from "../mocks/farm-data";

const lands = ref(
    mockLands.map(land => ({ ...land }))
);

const devices = ref(
    mockDevices.map(device => ({ ...device }))
);

const addLand = (formData) => {
    const area = Number(formData.area);
    const longitude = Number(formData.longitude);
    const latitude = Number(formData.latitude);

    if (!formData.name?.trim() || !formData.type?.trim() || !formData.location?.trim()) {
        throw new Error('土地名称、类型和位置不能为空');
    }

    if (!Number.isFinite(area) || area <= 0) {
        throw new Error('土地面积必须大于 0');
    }

    if (!Number.isFinite(longitude) || longitude < -180 || longitude > 180) {
        throw new Error('经度必须在 -180 到 180 之间');
    }

    if (!Number.isFinite(latitude) || latitude < -90 || latitude > 90) {
        throw new Error('纬度必须在 -90 到 90 之间');
    }

    const land = {
        ...formData,
        id: crypto.randomUUID(),
        name: formData.name.trim(),
        type: formData.type.trim(),
        location: formData.location.trim(),
        area,
        longitude,
        latitude
    };

    lands.value.push(land);
    return land;
};

const deleteLand = (landId) => {
    const deletedLand = lands.value.find(land => land.id === landId);

    if (!deletedLand) {
        throw new Error("不存在该土地，无法删除");
    }

    const haveBind = devices.value.some(device => device.landId === landId);

    if (haveBind) {
        throw new Error('该土地仍绑定设备，无法删除');
    }

    lands.value = lands.value.filter(land => land.id !== landId);

    return deletedLand;
};

const normalizeDeviceForm = (formData) => {
    const longitude = Number(formData.longitude);
    const latitude = Number(formData.latitude);

    if (!formData.name?.trim()) {
        throw new Error("设备名称不能为空");
    }
    if (!formData.type?.trim()) {
        throw new Error("设备类型不能为空");
    }
    if (!formData.model?.trim()) {
        throw new Error("设备型号不能为空");
    }
    if (formData.longitude === '' || formData.longitude === null || formData.longitude === undefined
        || !Number.isFinite(longitude) || longitude < -180 || longitude > 180) {
        throw new Error("经度必须在 -180 到 180 之间");
    }
    if (formData.latitude === '' || formData.latitude === null || formData.latitude === undefined
        || !Number.isFinite(latitude) || latitude < -90 || latitude > 90) {
        throw new Error("纬度必须在 -90 到 90 之间");
    }

    return {
        ...formData,
        name: formData.name.trim(),
        type: formData.type.trim(),
        model: formData.model.trim(),
        longitude,
        latitude
    };
};

const addDevice = (formData) => {
    const deviceData = normalizeDeviceForm(formData);

    const newDevice = {
        ...deviceData,
        id: crypto.randomUUID(),
        status: 'offline',
        battery: null,
        lastReportedAt: null
    };

    devices.value.push(newDevice);
    return newDevice;
};

const updateDevice = (deviceId, formData) => {
    const deviceIndex = devices.value.findIndex(device => device.id === deviceId);

    if (deviceIndex === -1) {
        throw new Error("设备不存在，无法更新");
    }

    const currentDevice = devices.value[deviceIndex];
    const deviceData = normalizeDeviceForm(formData);
    const updatedDevice = {
        ...currentDevice,
        ...deviceData,
        id: currentDevice.id,
        status: currentDevice.status,
        battery: currentDevice.battery,
        lastReportedAt: currentDevice.lastReportedAt
    };

    devices.value[deviceIndex] = updatedDevice;
    return updatedDevice;
};

const deleteDevice = (deviceId) => {
    const deletedDevice = devices.value.find(device => device.id === deviceId);

    if (!deletedDevice) {
        throw new Error("设备不存在，无法删除");
    }

    devices.value = devices.value.filter(device => device.id !== deviceId);

    return deletedDevice;
};

export const useFarmStore = () => {
    return {
        devices,
        lands,
        addLand,
        deleteLand,
        addDevice,
        updateDevice,
        deleteDevice
    };
};
