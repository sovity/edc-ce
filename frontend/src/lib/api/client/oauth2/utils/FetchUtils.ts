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
import {z} from 'zod';
import {type ClientCredentials} from '../model/ClientCredentials';
import {createUrlEncodedParamsString} from './HttpUtils';

const ClientCredentialsResponseSchema = z.object({
    access_token: z.string().min(1),
    token_type: z.string(),
    expires_in: z.number(),
    scope: z.string(),
});
export type ClientCredentialsResponse = z.infer<
    typeof ClientCredentialsResponseSchema
>;

export async function fetchClientCredentials(
    clientCredentials: ClientCredentials,
): Promise<ClientCredentialsResponse> {
    const response = await fetch(clientCredentials.tokenUrl, {
        method: 'POST',
        body: createUrlEncodedParamsString({
            grant_type: 'client_credentials',
            client_id: clientCredentials.clientId,
            client_secret: clientCredentials.clientSecret,
        }),
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
    });
    const json: unknown = await response.json();
    return parseTokenResponse(json);
}

function parseTokenResponse(json: unknown): ClientCredentialsResponse {
    const tokenResponseParsed = ClientCredentialsResponseSchema.safeParse(json);
    if (!tokenResponseParsed.success) {
        throw new Error('Bad access token response');
    }
    return tokenResponseParsed.data;
}
