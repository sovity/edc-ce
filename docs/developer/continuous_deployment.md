## Continuous Deployment

### Overview

A GitHub Actions workflow performs continuous build and deployment of the EDC Data Dashboard docker image to an [Azure Container Registry](https://docs.microsoft.com/azure/container-registry/container-registry-intro). The workflow runs automatically on commits to the "main" branch and uses the git commit id as docker image tag. 

## Initializing an Azure environment for CD

### Planning your deployment

You will need to provide the following:

- An Azure Container Registry (use preexisting or see [instructions](https://docs.microsoft.com/azure/container-registry/container-registry-get-started-azure-cli) how to set up)
- A service principal (instructions below)

### Create a service identity for GitHub Actions

[Create and configure an Azure AD application for GitHub Actions](https://docs.microsoft.com/azure/active-directory/develop/workload-identity-federation-create-trust-github).

Follow the instructions to *Create an app registration*.

- In **Supported Account Types**, select **Accounts in this organizational directory only**.
- Don't enter anything for **Redirect URI (optional)**.

Take note of the Application (client) ID.

Follow the instructions to [Configure a federated identity credential]([Configure a federated identity credential](https://docs.microsoft.com/azure/active-directory/develop/workload-identity-federation-create-trust-github?tabs=azure-portal#configure-a-federated-identity-credential)) for the `main` branch.

- For **Entity Type**, select **Branch**.
- For **GitHub branch name**, enter `main`.
- For **Name**, type any name.

[Grant the application Contributor permissions](https://docs.microsoft.com/azure/role-based-access-control/role-assignments-portal) for the corresponding ACR.

Configure the following GitHub secrets:

| Secret name         | Value                          |
| ------------------- | ------------------------------ |
| `ARM_CLIENT_ID`     | The application (client) ID.   |

### Configure CD settings

Configure the following GitHub secrets:

| Secret name                   | Value                                                        |
| ----------------------------- | ------------------------------------------------------------ |
| `ARM_TENANT_ID`               | The Azure AD tenant ID.                                      |
| `ARM_SUBSCRIPTION_ID`         | The Azure subscription ID to deploy resources in.            |
| `ACR_NAME`                    | The name of the Azure Container Registry to use. Should be already deployed. |

### Pushing ECD Data Dashboard image manually

You can push the EDC Data Dashboard Docker image to an Azure Container Registry (ACR) of your choice manually.

First authenticate with ACR by following the instructions in the [documentation](https://docs.microsoft.com/azure/container-registry/container-registry-authentication). Example for bash:

```bash
az login
az acr login --name <ACR_NAME>
```

Next, build the docker image by running the following Azure CLI command on your local machine, adapting the name and tag of the image according to your setup:

```bash
docker build -t <ACR_NAME>.azurecr.io/edc/edc-data-dashboard:latest .
```

Finally, push the docker image to ACR:

```bash
docker push <ACR_NAME>.azurecr.io/edc/edc-data-dashboard:latest
```
