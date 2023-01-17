# EDC UI

EDC UI by sovity for our extended EDC Connector.

## Configuration

This Angular codebase uses ENV vars in both local dev and production to
configure the application.

In general, all ENV vars `EDC_UI_*` will be written to a
`assets/app-config.json`, either before starting the angular build server or
before starting the nginx to serve static files.

All available configuration properties are documented in
`src/modules/app/config/app-config-properties.ts`

### Pass a JSON in an ENV Var

The ENV var `EDC_UI_CONFIG_JSON` can be used to pass a JSON that can contain all
properties that would otherwise need to be specified individually. Individually
provided ENV vars take precedence, however.

## Run dev mode

```shell
# Fake backend
(cd fake-backend && npm i && npm run start)

# Run Angular Application
npm i
npm run start
```

### Configuring Dev Mode

For dev mode ENV vars are read from:

- Current Environment Variables (highest precedence)
- `.env` file (not committed, in .gitignore)
- `.env.local-dev` file (defaults for working with fake backend).

```properties
# Example:
# Create a .env file to easily switch between profiles
EDC_UI_ACTIVE_PROFILE=mds-open-source
```

## Build docker image

```shell
# Build docker image
docker build -f "docker/Dockerfile" -t "edc-ui:latest" .

# Docker image will serve at :80
```

### Configuring docker image

At startup all ENV vars starting with `EDC_UI_` will be collected into a
`assets/app-config.json` and served.

## Codegen for EDC REST API

1. [optional] Update `openapi/openapi.yaml`.
2. in a shell execute
   ```shell
   docker run --rm -v "${PWD}:/local" openapitools/openapi-generator-cli generate -i /local/openapi/openapi.yaml -g typescript-angular -o /local/src/modules/edc-dmgmt-client/
   ```
   This re-generates the service and model classes.

> Please note that some client classes were edited manually after generation.
> When regenerating the classes for the API update be careful especially not to
> overwrite service `constructor` methods using the generator!
