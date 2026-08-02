import { request, withQuery } from './client';

export function listFarmTasks(filters) {
    return request(withQuery('/farm-tasks', filters));
}

export function createFarmTask(createRequest) {
    return request('/farm-tasks', {
        method: 'POST',
        body: createRequest
    });
}

export function startFarmTask(taskId) {
    return request(`/farm-tasks/${encodeURIComponent(taskId)}/start`, {
        method: 'POST'
    });
}

export function completeFarmTask(taskId, completeRequest) {
    return request(`/farm-tasks/${encodeURIComponent(taskId)}/complete`, {
        method: 'POST',
        body: completeRequest
    });
}

export function cancelFarmTask(taskId, cancelRequest) {
    return request(`/farm-tasks/${encodeURIComponent(taskId)}/cancel`, {
        method: 'POST',
        body: cancelRequest
    });
}
