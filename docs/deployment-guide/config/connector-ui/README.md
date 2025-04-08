
# sovity EDC UI Configuration

## Config

### Important

| Env Var                          | Required / Default | Description                                                                                                                         |
|----------------------------------|--------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| `NEXT_PUBLIC_MANAGEMENT_API_URL` | Required           | Management API URL                                                                                                                  |
| `NEXT_PUBLIC_MANAGEMENT_API_KEY` | Optional           | Management API Key to authenticate against the Management API. Only required for deployment scenarios that use a hard-coded API key | |

### Overrides

<details><summary>Show / Hide</summary>

| Env Var                        | Required / Default   | Description                                                                        |
|--------------------------------|----------------------|------------------------------------------------------------------------------------|
| `NEXT_PUBLIC_USE_FAKE_BACKEND` | Optional             | Local-dev only: Enable fake backend                                                |
| `NEXT_PUBLIC_BUILD_DATE`       | Default is set by CI | Container build date. Should be compatible with `new Date(...)` May be overridden. |
| `NEXT_PUBLIC_BUILD_VERSION`    | Default is set by CI | Container build release version or git description. May be overridden.             |
| `PORT`                         | Default is `8080`    | Port bound by the Node Server                                                      |
| `HOSTNAME`                     | Default is `0.0.0.0` | Address bound by the Node Server                                                   |

</details>
