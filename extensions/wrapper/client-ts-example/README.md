<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />API Wrapper &amp; API Client:<br />TypeScript API Client Example</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=bug_report.md">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues/new?template=feature_request.md">Request Feature</a>
  </p>
</div>

## About this component

Example Project that consumes the TypeScript API Client Library.

The dependency itself is not part of the package.json as it is expected to be built and linked via `npm link`.

## Getting Started

From the root folder of this repository execute the following:

```shell script
# Use WSL or Git Bash

# Build Dev EDC
docker build -f "connector/Dockerfile" -t "edc-dev-for-api-wrapper" --build-arg BUILD_ARGS="-Pdmgmt-api-key" .

# Fetch up-to-date UI
docker compose -f docker-compose-dev.yaml pull

# Launch Dev EDCs
DEV_EDC_IMAGE=edc-dev-for-api-wrapper EDC_UI_ACTIVE_PROFILE=sovity-open-source docker compose -f docker-compose-dev.yaml up --scale postgresql=0 --scale postgresql2=0 -d

# Generate OpenAPI & TypeScript Code
./gradlew :extensions:wrapper:wrapper:clean :extensions:wrapper:wrapper:build

# Build Client Library
(cd extensions/wrapper/client-ts && npm install && npm run build)

# Run Example Project
(cd extensions/wrapper/client-ts-example && npm install && npm link ../client-ts && npm run dev)

# Shut down Dev EDCs
docker compose -f docker-compose-dev.yaml down -t 1
```

## License

Apache License 2.0 - see [LICENSE](../../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
