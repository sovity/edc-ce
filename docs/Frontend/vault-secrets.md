---
icon: building-lock
---

# Vault Secrets UI

The new **Vault Secrets** functionality provides a user-friendly interface for managing secrets directly within the UI.
Previously, secrets such as OAuth2 credentials were managed exclusively via API calls.
Now, you can create, edit, and delete secrets using the UI, making secret management easier, more accessible, and less error-prone.

## Features

- **Create Vault Secrets:** Easily add new secrets with a key, value, and description
- **Edit Vault Secrets:** Modify existing secrets including their value and description
- **Delete Vault Secrets:** Remove secrets that are no longer needed
- **Search & Reference:** Quickly find secrets using a search bar and reference them in other dialogs or configurations

## Usage

### Viewing Vault Secrets

The Vault Secrets list shows all stored secrets with their key names and descriptions.
Use the search box to filter secrets by key name or description.

![Vault Secrets List](/docs/images/vault-secrets.png)

Click the **New Vault Secret** button to open the creation dialog.
Provide the secret key, secret value, and a descriptive note for clarity.

### Editing an Existing Vault Secret

Select a secret from the list and click the edit option under the three dots.
You can update the secret value and description here.

![Edit Vault Secret](/docs/images/vault-secrets-edit.png)

### Searching and Referencing Secrets

When referencing secrets in other parts of the UI, use the search dropdown to find the secret.
You can also create a new secret directly from the search dialog if it does not exist.

![Search and Reference Secrets](/docs/images/vault-secrets-reference.png)

## Notes

- Secret values are securely stored and masked in the UI
- Overwriting existing secrets requires explicit confirmation
- Make sure to update references to secrets if you change keys or values
