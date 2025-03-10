
# sovity EDC UI Configuration

## Config Precedence

From highest to lowest:

- Config fetched from backend
- Config fetched from backend: Content of `EDC_UI_CONFIG_JSON`
- Config via env vars
- Config via env var `EDC_UI_CONFIG_JSON`

## Config fetched from backend

Our EDC Connector forwards all EDC Configuration that starts with `edc.ui.` to the EDC UI.

That means, except for the information on how the UI connects to the backend's Management API, most UI config can also be configured on the backend.

## Config

### Important

| Env Var                                   | Required / Default                                    | Description                                                                                                                                                                                          |
|-------------------------------------------|-------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `EDC_UI_ACTIVE_PROFILE`                   | Defaults to `sovity-open-source`                      | Customer-Specific Feature Set and/or Theme. Available values: `sovity-open-source`. See `edc-ui-profile-data.ts` |
| `EDC_UI_MANAGEMENT_API_KEY`               | Optional                                              | Management API Key to authenticate against the Management API. Only required for deployment scenarios that use a hard-coded API key                                                                  |
| `EDC_UI_MANAGEMENT_API_URL`               | Required                                              | Management API URL as reachable by the browser.                                                                                                                                                      |

### Optional

| Env Var                                                | Required / Default                                    | Description                                                                                                                                                                                                                                   |
|--------------------------------------------------------|-------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `EDC_UI_BUILD_DATE`                                    | Default is set by CI                                  | Container build date. Should be compatible with `new Date(...)` May be overridden.                                                                                                                                                            |
| `EDC_UI_BUILD_VERSION`                                 | Default is set by CI                                  | Container build release version or git description. May be overridden.                                                                                                                                                                        |
| `EDC_UI_CATALOG_URLS`                                  | Optional                                              | Pre-configured Other Connector Endpoints to be used in catalog browser, comma separated. <br />**Note:** For dataspaces that required Participant IDs for catalog access, ensure each Connector Endpoint is suffixed by `?participantId=...`! |
| `EDC_UI_MANAGEMENT_API_URL_SHOWN_IN_DASHBOARD`         | Optional                                              | Management API URL to be advertised in dashboard for API users. Required for scenarios where the Management API URL differs for UI users and API users.                                                                                       |
| `NGINX_ACCESS_LOG`                                     | Defaults to `/dev/stdout`                             | Nginx Access Log configuration.<br />Can be set to `off` to disable.                                                                                                                                                                          |
| `NGINX_BIND`                                           | Defaults to `0.0.0.0`                                 | Nginx Bind Address: Host<br />Must be configured correctly for health checks to work<br />Only available in production                                                                                                                        |
| `NGINX_ERROR_LOG`                                      | Defaults to `/dev/stderr`                             | Nginx Error Log configuration.<br/>Can be set to `off` to disable.                                                                                                                                                                            |
| `NGINX_PORT`                                           | Defaults to `8080`                                    | Nginx Bind Address: Port<br />Must be configured correctly for health checks to work<br />Only available in production                                                                                                                        |

### Overrides

<details><summary>Show / Hide</summary>

| Env Var                   | Required / Default           | Description                                                                                                                                                                                                                                         |
|---------------------------|------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `EDC_UI_CONFIG_JSON`      | Optional                     | Instead of providing multiple ENV Vars, provide a single one as JSON.<br />Individual ENV Vars will take precedence over this JSON.                                                                                                                 |
| `EDC_UI_CONFIG_URL`       | Defaults to `/edc-ui-config` | URL to fetch a Config JSON from that will take precedence. This allows an EDC Backend Extension to provide EDC UI configuration. If this URL is relative, it will be appended to the Management API Url. Can be turned off using the value `false`. |
| `EDC_UI_USE_FAKE_BACKEND` | Optional                     | Local-dev only: Enable fake backend                                                                                                                                                                                                                 |

</details>
