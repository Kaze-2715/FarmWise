import { request, withQuery } from './client';

export function listDevices(filters = {}) {
    return request(withQuery('/devices', filters));
}

export function createDevice(createDeviceRequest) {
    return request('/devices', {
        method: 'POST',
        body: createDeviceRequest
    });
}

export function updateDevice(deviceId, updateDeviceRequest) {
    return request(`/devices/${encodeURIComponent(deviceId)}`, {
        method: 'PUT',
        body: updateDeviceRequest
    });
}

export function deleteDevice(deviceId) {
    return request(`/devices/${encodeURIComponent(deviceId)}`, {
        method: 'DELETE'
    });
}
