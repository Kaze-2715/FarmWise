import { request, withQuery } from './client';

const configPath = landId =>
    `/lands/${encodeURIComponent(landId)}/irrigation-configs`;

export function listIrrigationConfigs(landId) {
    return request(configPath(landId));
}

export function createIrrigationConfig(landId, createRequest) {
    return request(configPath(landId), {
        method: 'POST',
        body: createRequest
    });
}

export function updateIrrigationConfig(landId, configId, updateRequest) {
    return request(`${configPath(landId)}/${encodeURIComponent(configId)}`, {
        method: 'PUT',
        body: updateRequest
    });
}

export function enableIrrigationConfig(landId, configId) {
    return request(`${configPath(landId)}/${encodeURIComponent(configId)}/enable`, {
        method: 'POST'
    });
}

export function deleteIrrigationConfig(landId, configId) {
    return request(`${configPath(landId)}/${encodeURIComponent(configId)}`, {
        method: 'DELETE'
    });
}

export function listIrrigationRecords(filters) {
    return request(withQuery('/irrigation-records', filters));
}

export function startIrrigation(startRequest) {
    return request('/irrigations', {
        method: 'POST',
        body: startRequest
    });
}

export function stopIrrigation(recordId) {
    return request(`/irrigations/${encodeURIComponent(recordId)}/stop`, {
        method: 'POST'
    });
}
