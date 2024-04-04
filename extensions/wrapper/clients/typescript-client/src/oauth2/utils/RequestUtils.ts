export function injectAccessTokenHeader(init: RequestInit, token: string) {
    init.headers = withHeader('Authorization', `Bearer ${token}`, init.headers);
}

function withHeader(
    headerName: string,
    headerValue: string,
    headers?: HeadersInit,
): HeadersInit {
    if (!headers) {
        headers = {};
        headers[headerName] = headerValue;
        return headers;
    }

    if (Array.isArray(headers)) {
        return headers.map(([a, b]) =>
            a !== headerName ? [a, b] : [headerName, headerValue],
        );
    }

    if (isHeaders(headers)) {
        if (headers.has(headerName)) {
            headers.set(headerName, headerValue);
        } else {
            headers.append(headerName, headerValue);
        }
        return headers;
    }

    headers[headerName] = headerValue;
    return headers;
}

function isHeaders(object: any): object is Headers {
    return (
        'append' in object &&
        'delete' in object &&
        'get' in object &&
        'has' in object &&
        'set' in object &&
        'forEach' in object
    );
}
