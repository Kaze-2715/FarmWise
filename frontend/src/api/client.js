const API_BASE_URL = '/api';

const NO_REFRESH_PATHS = new Set([
    '/auth/login',
    '/auth/register',
    '/auth/refresh'
]);

let accessToken = null;

let refreshPromise = null;

let unauthorizedHandler = null;

export function setUnauthorizedHandler(handler) {
    unauthorizedHandler = handler;
}

export function setAccessToken(token) {
    accessToken = token;
}

export function clearAccessToken() {
    accessToken = null;
}

export function withQuery(path, params = {}) {
    const searchParams = new URLSearchParams();

    Object.entries(params).forEach(([key, value]) => {
        if (value === null || value === undefined || value === '') {
            return;
        }

        searchParams.set(
            key,
            value instanceof Date ? value.toISOString() : String(value)
        );
    });

    const queryString = searchParams.toString();
    return queryString ? `${path}?${queryString}` : path;
}

async function performTokenRefresh() {
    const response = await fetch(`${API_BASE_URL}/auth/refresh`, {
        method: 'POST',
        credentials: 'include'
    });

    if (!response.ok) {
        clearAccessToken();
        unauthorizedHandler?.();
        return false;
    }

    const data = await response.json();
    setAccessToken(data.accessToken);
    return true;
}

async function refreshAccessToken() {
    if (refreshPromise) {
        return refreshPromise;
    }
    refreshPromise = performTokenRefresh();

    try {
        return await refreshPromise;
    } finally {
        refreshPromise = null;
    }
}

export async function request(path, options = {}, retried = false) {
    const hasBody = options.body != null;
    const isFormData = hasBody && options.body instanceof FormData;

    const response = await fetch(`${API_BASE_URL}${path}`, {
        ...options,
        credentials: 'include',
        headers: {
            ...(hasBody && !isFormData ? { 'Content-Type': 'application/json' } : {}),
            ...(accessToken ? { Authorization: `Bearer ${accessToken}` } : {}),
            ...options.headers
        },
        body: hasBody
            ? (isFormData ? options.body : JSON.stringify(options.body))
            : undefined
    });

    if (response.status === 401 && !retried && !NO_REFRESH_PATHS.has(path)) {
        const refreshed = await refreshAccessToken();

        if (refreshed) {
            return request(path, options, true);
        }
    }

    const contentType = response.headers.get('Content-Type') || '';

    const data = contentType.includes('application/json') ? await response.json() : undefined;

    if (!response.ok) {
        throw new Error(data?.message || `请求失败，状态码：${response.status}`);
    }

    return data;
}
