<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-extensions">
    <img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">EDC-Connector Extension:<br />API Wrapper &amp; API Client:<br />Quarkus Example Project</h3>

  <p align="center">
    <a href="https://github.com/sovity/edc-extensions/issues">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-extensions/issues">Request Feature</a>
  </p>
</div>

## About this component

Example Quarkus Application that uses our Java API Client Library.

## Getting Started

From the root folder of this repository execute the following:

```shell script
# Use WSL or Git Bash

# Build Dev EDC
docker build -f "connector/Dockerfile" -t "edc-dev-for-api-wrapper" --build-arg BUILD_ARGS="-Pdev-edc" .

# Fetch up-to-date UI
docker compose -f docker-compose-dev.yaml pull

# Launch Dev EDCs
EDC_IMAGE=edc-dev-for-api-wrapper EDC_UI_ACTIVE_PROFILE=sovity-open-source docker compose -f docker-compose-dev.yaml up --scale postgresql=0 --scale postgresql2=0 -d

# Launch Quarkus Application
./gradlew :extensions:wrapper:client-example:quarkusDev

# Shut down Dev EDCs
docker compose -f docker-compose-dev.yaml down -t 1
```

## License

Apache License 2.0 - see [LICENSE](../../LICENSE)

## Contact

sovity GmbH - contact@sovity.de
