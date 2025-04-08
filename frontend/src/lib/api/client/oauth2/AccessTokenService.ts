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
import {type ClientCredentials} from './model/ClientCredentials';
import {
    type ClientCredentialsResponse,
    fetchClientCredentials,
} from './utils/FetchUtils';

export class AccessTokenService {
    private activeRequest: Promise<ClientCredentialsResponse> | null = null;
    private refreshTimeout?: number;
    private accessToken: string | null = null;

    constructor(private clientCredentials: ClientCredentials) {}

    async getAccessToken(): Promise<string> {
        if (!this.accessToken) {
            return await this.refreshAccessToken();
        }
        return this.accessToken;
    }

    /**
     * Synchronized refreshing of the access token
     */
    async refreshAccessToken(): Promise<string> {
        if (this.activeRequest) {
            await this.activeRequest;
            return this.accessToken!;
        }

        this.accessToken = null;
        this.activeRequest = fetchClientCredentials(this.clientCredentials);
        const response = await this.activeRequest;
        this.scheduleNextRefresh(response);
        this.accessToken = response.access_token;
        this.activeRequest = null;

        return this.accessToken;
    }

    private scheduleNextRefresh(response: ClientCredentialsResponse) {
        clearTimeout(this.refreshTimeout);
        const ms = (response.expires_in - 2) * 1000;
        // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
        this.refreshTimeout = setTimeout(() => {
            this.accessToken = null;
        }, ms) as any;
    }
}
