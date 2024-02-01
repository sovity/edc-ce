import {withHeader} from './RequestUtils';

export class AccessTokenInjector {
    // init fields are passed by reference
    inject(init: RequestInit, token: string) {
        const authorizationValue = this.buildBearerValueString(token);
        init.headers = withHeader(
            'Authorization',
            authorizationValue,
            init.headers,
        );
    }

    private buildBearerValueString(token: string): string {
        return `Bearer ${token}`;
    }
}
