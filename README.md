<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->

<a name="readme-top"></a>

<!-- PROJECT SHIELDS -->

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url] [![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/sovity/edc-ui">
    <img src="src/assets/images/sovity_logo.svg" alt="Logo" width="300">
  </a>

<h3 align="center">sovity EDC UI</h3>

  <p align="center">
    UI for sovity's extended EDC-Connector.
    <br />
    <a href="https://github.com/sovity/edc-ui/issues">Report Bug</a>
    ·
    <a href="https://github.com/sovity/edc-ui/issues">Request Feature</a>
    <br />
    <br />
    <a href="https://angular.io"><img src="https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white" alt="angular.io" /></a>
  </p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#about-the-project">About The Project</a></li>
    <li><a href="#compatibility">Compatibility</a></li>
    <li><a href="#getting-started">Getting Started</a></li>
    <li><a href="#configuration">Configuration</a></li>
    <li><a href="#running-dev-mode">Running dev mode</a></li>
    <li><a href="#build-docker-image">Build docker image</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->

## About The Project

[Eclipse Dataspace Components](https://github.com/eclipse-edc) is a framework
for building dataspaces, exchanging data securely with ensured data
sovereignity.

[sovity](https://sovity.de/) extends the EDC functionality to offer
enterprise-ready managed "Connector-as-a-Service" services, bringing
out-of-the-box fully configured DAPS and integrations to existing other
dataspace technologies.

Our extension of EDC DataDashboard functionalities has been made open source and
will be kept compatible to mostly stock EDCs with minimal API extending
extensions.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<div>
  <div style="float:left;width:33%;">
    <img src="docs/screenshots/screenshot-dashboard.png" style="max-width:300px;">
  </div>
  <div style="float:left;width:33%;">
    <img src="docs/screenshots/screenshot-contracts.png" style="max-width:300px;">
  </div>
  <div style="float:left;width:33%;">
    <img src="docs/screenshots/screenshot-assets.png" style="max-width:300px;">
  </div>
</div>

<br style="clear:both;"/>

<!-- GETTING STARTED -->

## Getting Started

The fastest way to get started is using our Getting Started Guide in
[sovity EDC CE Getting Started Guide](https://github.com/sovity/edc-extensions#getting-started).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONFIGURATION -->

## Configuration

A list of all available configuration properties can be found
[here](src/app/core/config/app-config-properties.ts).

In general, all ENV vars `EDC_UI_*` are written to an `assets/app-config.json`,
either before starting the angular build server or before starting the nginx to
serve static files.

### (Optional) Pass a JSON in an ENV Var

The ENV var `EDC_UI_CONFIG_JSON` can be used to pass a JSON that can contain all
properties that would otherwise need to be specified individually. Individually
provided ENV vars take precedence, however.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### (Optional) Configuring the NGINX

```yaml
# Customizable ENV Vars and their defaults
NGINX_BIND: '0.0.0.0'
NGINX_PORT: '8080'
NGINX_ACCESS_LOG: '/dev/stdout'
NGINX_ERROR_LOG: '/dev/stderr'
```

<!-- RUNNING DEV MODE -->

## Running dev mode

Requires Node.js version `^16.10.0`.

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

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- BUILD DOCKER IMAGE -->

## Build docker image

Requires docker.

```shell
# Build docker image
docker build -f "docker/Dockerfile" -t "edc-ui:latest" .

# Docker image will serve at :80
```

### Configuring docker image

ENV vars `EDC_UI_*` will be collected into `assets/app-config.json` at container
startup and served.

`NGINX_BIND` and `NGINX_PORT` are templated into `default.conf` and can be used
to overwrite nginx bind address and port.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTRIBUTING -->

## Contributing

Contributions are what make the open source community such an amazing place to
learn, inspire, and create. Any contributions you make are **greatly
appreciated**.

If you have a suggestion that would make this better, please fork the repo and
create a pull request. You can also simply open an issue with the tag
"enhancement". Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

Our contribution guide can be found in [CONTRIBUTING.md](CONTRIBUTING.md).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- LICENSE -->

## License

Distributed under the Apache 2.0 License. See `LICENSE` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTACT -->

## Contact

contact@sovity.de

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[contributors-shield]:
  https://img.shields.io/github/contributors/sovity/edc-ui.svg?style=for-the-badge
[contributors-url]: https://github.com/sovity/edc-ui/graphs/contributors
[forks-shield]:
  https://img.shields.io/github/forks/sovity/edc-ui.svg?style=for-the-badge
[forks-url]: https://github.com/sovity/edc-ui/network/members
[stars-shield]:
  https://img.shields.io/github/stars/sovity/edc-ui.svg?style=for-the-badge
[stars-url]: https://github.com/sovity/edc-ui/stargazers
[issues-shield]:
  https://img.shields.io/github/issues/sovity/edc-ui.svg?style=for-the-badge
[issues-url]: https://github.com/sovity/edc-ui/issues
[license-shield]:
  https://img.shields.io/github/license/sovity/edc-ui.svg?style=for-the-badge
[license-url]: https://github.com/sovity/edc-ui/blob/master/LICENSE.txt
[linkedin-shield]:
  https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/company/sovity
[Angular.io]:
  https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white
[Angular-url]: https://angular.io/
