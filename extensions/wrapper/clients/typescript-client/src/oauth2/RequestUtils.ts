import {isHeaders} from './ObjectUtils';

export function withHeader(
    headerName: string,
    headerValue: string,
    headers?: HeadersInit,
): HeadersInit {
    if (!headers) {
        headers = {headerName: headerValue};
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

    return {
        ...(headers as Record<string, string>),
        headerName: headerValue,
    };
}
