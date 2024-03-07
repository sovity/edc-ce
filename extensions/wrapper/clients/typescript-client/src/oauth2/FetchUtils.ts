import {z} from 'zod';

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
    url: string,
    credentialsBody: string,
): Promise<ClientCredentialsResponse> {
    let response = await fetch(url, {
        method: 'POST',
        body: credentialsBody,
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
