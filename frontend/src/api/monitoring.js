import { request, withQuery } from './client';

const thresholdPath = landId =>
    `/lands/${encodeURIComponent(landId)}/environment-thresholds`;

export function listSensorReadings(filters) {
    return request(withQuery('/sensor-readings', filters));
}

export function listLatestSensorReadings(landId) {
    return request(withQuery('/sensor-readings/latest', { landId }));
}

export function listEnvironmentThresholds(landId) {
    return request(thresholdPath(landId));
}

export function createEnvironmentThreshold(landId, createRequest) {
    return request(thresholdPath(landId), {
        method: 'POST',
        body: createRequest
    });
}

export function updateEnvironmentThreshold(landId, metric, updateRequest) {
    return request(
        `${thresholdPath(landId)}/${encodeURIComponent(metric)}`,
        {
            method: 'PUT',
            body: updateRequest
        }
    );
}

export function deleteEnvironmentThreshold(landId, metric) {
    return request(`${thresholdPath(landId)}/${encodeURIComponent(metric)}`, {
        method: 'DELETE'
    });
}
