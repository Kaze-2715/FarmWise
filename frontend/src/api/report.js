import { request, withQuery } from './client';

export function listReports(filters = {}) {
    return request(withQuery('/reports', filters));
}

export function getReport(reportId) {
    return request(`/reports/${encodeURIComponent(reportId)}`);
}

export function generateReport(generateRequest) {
    return request('/reports', {
        method: 'POST',
        body: generateRequest
    });
}

export function archiveReport(reportId) {
    return request(`/reports/${encodeURIComponent(reportId)}/archive`, {
        method: 'POST'
    });
}
