import { request, withQuery } from './client';

const conversationPath = conversationId =>
    `/ai/conversations/${encodeURIComponent(conversationId)}`;

export function listConversations(filters) {
    return request(withQuery('/ai/conversations', filters));
}

export function getConversation(conversationId) {
    return request(conversationPath(conversationId));
}

export function createConversation(createRequest) {
    return request('/ai/conversations', {
        method: 'POST',
        body: createRequest
    });
}

export function sendMessage(conversationId, sendRequest) {
    return request(`${conversationPath(conversationId)}/messages`, {
        method: 'POST',
        body: sendRequest
    });
}

export function createTaskFromMessage(
    conversationId,
    messageId,
    createRequest
) {
    return request(
        `${conversationPath(conversationId)}/messages/${encodeURIComponent(messageId)}/task`,
        {
            method: 'POST',
            body: createRequest
        }
    );
}

export function closeConversation(conversationId) {
    return request(`${conversationPath(conversationId)}/close`, {
        method: 'POST'
    });
}
