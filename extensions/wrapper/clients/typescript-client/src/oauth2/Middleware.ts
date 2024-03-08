import {Middleware, RequestContext, ResponseContext} from '../generated';
import {AccessTokenService} from './AccessTokenService';
import {ClientCredentials} from './model/ClientCredentials';
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
