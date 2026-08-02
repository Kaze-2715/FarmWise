import { request, withQuery } from './client';

export function listAlerts(filters) {
    return request(withQuery('/alerts', filters));
}

export function createAlert(createRequest) {
    return request('/alerts', {
        method: 'POST',
        body: createRequest
    });
}

export function startAlert(alertId, startRequest) {
    return request(`/alerts/${encodeURIComponent(alertId)}/start`, {
        method: 'POST',
        body: startRequest
    });
}

export function resolveAlert(alertId, resolveRequest) {
    return request(`/alerts/${encodeURIComponent(alertId)}/resolve`, {
        method: 'POST',
        body: resolveRequest
    });
}

export function ignoreAlert(alertId, ignoreRequest) {
    return request(`/alerts/${encodeURIComponent(alertId)}/ignore`, {
        method: 'POST',
        body: ignoreRequest
    });
}
