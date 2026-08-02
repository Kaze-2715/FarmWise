import { request } from './client';

export function listLands() {
    return request('/lands');
}

export function createLand(createLandRequest) {
    return request('/lands', {
        method: 'POST',
        body: createLandRequest
    });
}

export function updateLand(landId, updateLandRequest) {
    return request(`/lands/${encodeURIComponent(landId)}`, {
        method: 'PUT',
        body: updateLandRequest
    });
}

export function deleteLand(landId) {
    return request(`/lands/${encodeURIComponent(landId)}`, {
        method: 'DELETE'
    });
}
