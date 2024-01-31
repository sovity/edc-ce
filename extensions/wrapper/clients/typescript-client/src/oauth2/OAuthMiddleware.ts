import {Middleware, RequestContext, ResponseContext} from '../generated';
import {AccessTokenInjector} from './AccessTokenInjector';
import {AccessTokenStore} from './AccessTokenStore';
import {needsAuthentication} from './HttpUtils';

export class OAuthMiddleware {
    accessTokenInjector: AccessTokenInjector;
    accessTokenStore: AccessTokenStore;

    constructor(
        accessTokenInjector: AccessTokenInjector,
        accessTokenStore: AccessTokenStore,
    ) {
        this.accessTokenInjector = accessTokenInjector;
        this.accessTokenStore = accessTokenStore;
    }

    build(): Middleware {
        return {
            pre: async (ctx: RequestContext) => {
                if (!this.accessTokenStore.accessToken) {
                    await this.accessTokenStore.refreshToken();
                }

                this.injectAccessTokenHeader(ctx.init);

                return Promise.resolve({
                    url: ctx.url,
                    init: ctx.init,
                });
            },
            post: async (ctx: ResponseContext): Promise<Response> => {
                if (needsAuthentication(ctx.response.status)) {
                    await this.accessTokenStore.refreshToken();
                    this.injectAccessTokenHeader(ctx.init);
                    // Use normal fetch to not trigger middleware on retry
                    return await fetch(ctx.url, ctx.init);
                }

                return Promise.resolve(ctx.response);
            },
        };
    }

    private injectAccessTokenHeader(req: RequestInit) {
        if (this.accessTokenStore.accessToken) {
            this.accessTokenInjector.inject(
                req,
                this.accessTokenStore.accessToken,
            );
        }
    }
}
