import { request } from './client';

export function uploadFile(file, purpose) {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('purpose', purpose);

    return request('/files', {
        method: 'POST',
        body: formData
    });
}

export function getFileContentUrl(fileId) {
    return `/api/files/${encodeURIComponent(fileId)}/content`;
}
