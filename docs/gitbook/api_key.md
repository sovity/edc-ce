Api Key
========

The EDC management API is protected by an API key.

The API key can and must be set in the EDC backend configuration:
```yaml
# Management API Key
EDC_API_AUTH_KEY: ApiKeyDefaultValue
```

So that the EDC UI can retrieve the data from the backend management API, the API key must also be made known to the UI:
```yaml
# Management API Key
EDC_UI_MANAGEMENT_API_KEY: "ApiKeyDefaultValue"
```

For a complete list of setting options, see for example the [backend settings](https://github.com/sovity/edc-extensions/blob/main/docs/deployment-guide/goals/production/README.md#edc-backend-configuration) and [UI settings](https://github.com/sovity/edc-extensions/blob/main/docs/deployment-guide/goals/production/README.md#edc-ui-configuration) for productive deployment.
