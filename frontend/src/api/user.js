import { request } from "./client";

export function getCurrentUser() {
    return request('/users/me');
}

export function updateCurrentUser(updateRequest) {
    return request('/users/me', {
        method: 'PUT',
        body: updateRequest
    });
}
