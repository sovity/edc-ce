/*
 * Copyright 2025 sovity GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 * Contributors:
 *     sovity - init and continued development
 */
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
        return headers.map(([a, b]): [string, string] =>
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
