import {z} from 'zod';
import {ClientCredentials} from '../model/ClientCredentials';
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
    let response = await fetch(clientCredentials.tokenUrl, {
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
    let json: unknown = await response.json();
    return parseTokenResponse(json);
}

function parseTokenResponse(json: unknown): ClientCredentialsResponse {
    const tokenResponseParsed = ClientCredentialsResponseSchema.safeParse(json);
    if (!tokenResponseParsed.success) {
        throw new Error('Bad access token response');
    }
    return tokenResponseParsed.data;
}
