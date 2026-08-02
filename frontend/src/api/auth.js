import { request, setAccessToken, clearAccessToken } from "./client";

export async function login(loginRequest) {
    const response = await request('/auth/login', {
        method: 'POST',
        body: loginRequest
    });

    setAccessToken(response.accessToken);
    return response;
}

export function sendVerificationCode(verificationCodeRequest) {
    return request('/auth/verification-codes', {
        method: 'POST',
        body: verificationCodeRequest
    });
}

export function register(registerRequest) {
    return request('/auth/register', {
        method: 'POST',
        body: registerRequest
    });
}

export async function logout() {
    await request('/auth/logout', {
        method: 'POST'
    });

    clearAccessToken();
}