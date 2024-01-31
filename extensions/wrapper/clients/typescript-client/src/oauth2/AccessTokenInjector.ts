import {isHeaders} from './TypeUtils';

export class AccessTokenInjector {
    // init fields are passed by reference
    inject(init: RequestInit, token: string) {
        const authorizationValue = this.buildBearerValueString(token);

        if (!init.headers) {
            init.headers = {Authorization: authorizationValue};
            return;
        }

        if (Array.isArray(init.headers)) {
            init.headers = init.headers.filter(
                ([a, b]) => a !== 'Authorization',
            );
            init.headers.push(['Authorization', authorizationValue]);
        } else if (isHeaders(init.headers)) {
            if (init.headers.has('Authorization')) {
                init.headers.set('Authorization', authorizationValue);
            } else {
                init.headers.append('Authorization', authorizationValue);
            }
        } else {
            init.headers = {
                ...(init.headers as Record<string, string>),
                Authorization: authorizationValue,
            };
        }
    }

    private buildBearerValueString(token: string): string {
        return `Bearer ${token}`;
    }
}
