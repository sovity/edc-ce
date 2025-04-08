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
import {
    type Middleware,
    type RequestContext,
    type ResponseContext,
} from '../generated';
import {AccessTokenService} from './AccessTokenService';
import {type ClientCredentials} from './model/ClientCredentials';
import {needsAuthentication} from './utils/HttpUtils';
import {injectAccessTokenHeader} from './utils/RequestUtils';

export function buildOAuthMiddleware(
    clientCredentials: ClientCredentials,
): Middleware {
    const accessTokenService = new AccessTokenService(clientCredentials);

    return {
        pre: async (ctx: RequestContext) => {
            const accessToken = await accessTokenService.getAccessToken();
            injectAccessTokenHeader(ctx.init, accessToken);

            return Promise.resolve({
                url: ctx.url,
                init: ctx.init,
            });
        },
        post: async (ctx: ResponseContext): Promise<Response> => {
            if (needsAuthentication(ctx.response.status)) {
                const accessToken =
                    await accessTokenService.refreshAccessToken();
                injectAccessTokenHeader(ctx.init, accessToken);
                // Use normal fetch to not trigger middleware on retry
                return await fetch(ctx.url, ctx.init);
            }

            return Promise.resolve(ctx.response);
        },
    };
}
