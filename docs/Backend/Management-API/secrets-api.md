# Connector Vault Secrets API Documentation

This API is designed for managing and creating secrets in the Connector Vault, which are typically used for OAuth2 flows.

## Key Concepts

- **`secret-alias`**: Represents the unique identifier (alias) for the secret in the vault. It acts as a reference to retrieve or manage the stored secret.
- **`my-secret`**: Represents the actual value of the secret that needs to be stored. This could be sensitive data, such as client credentials or tokens.

## Endpoints

### 1. Create a Secret
Use this endpoint to create a new secret in the Connector Vault.

**Endpoint**:  
`POST {{EDC_MANAGEMENT_URL}}/v3/secrets`

**Request Body**:
{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
    "@context": {
        "edc": "https://w3id.org/edc/v0.0.1/ns/"
    },
    "@type": "Secret",
    "@id": "secret-alias",
    "edc:value": "my-secret"
}
```
{% endcode %}

- `@id`: Unique identifier (alias) for the secret.
- `edc:value`: The secret value to be stored.


### 2. Retrieve a Secret
Use this endpoint to retrieve a secret by its unique identifier.

**Endpoint**:  
`GET {{EDC_MANAGEMENT_URL}}/v3/secrets/:secret-id`

- Replace `:secret-id` with the unique identifier (alias) of the secret.

### 3. Delete a Secret
Use this endpoint to delete a stored secret by its unique identifier.

**Endpoint**:  
`DELETE {{EDC_MANAGEMENT_URL}}/v3/secrets/:secret-id`

- Replace `:secret-id` with the unique identifier (alias) of the secret.

### 4. Update a Secret
Use this endpoint to update the value of an existing secret.

**Endpoint**:  
`PUT {{EDC_MANAGEMENT_URL}}/v3/secrets`

**Request Body**:
{% code title="JSON" overflow="wrap" lineNumbers="true" %}
```json
{
    "@context": {
        "edc": "https://w3id.org/edc/v0.0.1/ns/"
    },
    "@type": "Secret",
    "@id": "secret-id",
    "edc:value": "my-updated-secret"
}
```
{% endcode %}

- `@id`: Unique identifier (alias) for the secret.
- `edc:value`: The updated secret value to be stored.

## Limitations
- You cannot add json structures to the vault. This is necessary if you want to add AWS-S3 Credentials to the vault.
