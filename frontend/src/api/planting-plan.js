import { request, withQuery } from './client';

export function listPlantingPlans(landId) {
    return request(withQuery('/planting-plans', { landId }));
}

export function createPlantingPlan(createRequest) {
    return request('/planting-plans', {
        method: 'POST',
        body: createRequest
    });
}

export function updatePlantingPlan(planId, updateRequest) {
    return request(`/planting-plans/${encodeURIComponent(planId)}`, {
        method: 'PUT',
        body: updateRequest
    });
}

export function updatePlantingPlanStatus(planId, statusRequest) {
    return request(`/planting-plans/${encodeURIComponent(planId)}/status`, {
        method: 'PUT',
        body: statusRequest
    });
}

export function deletePlantingPlan(planId) {
    return request(`/planting-plans/${encodeURIComponent(planId)}`, {
        method: 'DELETE'
    });
}
