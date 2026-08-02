import { request, withQuery } from './client';

export function listUsers(filters = {}) {
    return request(withQuery('/admin/users', filters));
}

export function updateUserRoles(userId, updateRequest) {
    return request(`/admin/users/${encodeURIComponent(userId)}/roles`, {
        method: 'PUT',
        body: updateRequest
    });
}

export function listRoles() {
    return request('/admin/roles');
}

export function listPermissions(module) {
    return request(withQuery('/admin/permissions', { module }));
}

export function updateRolePermissions(roleCode, updateRequest) {
    return request(
        `/admin/roles/${encodeURIComponent(roleCode)}/permissions`,
        {
            method: 'PUT',
            body: updateRequest
        }
    );
}
