<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->

<a name="readme-top"></a>

<!-- PROJECT SHIELDS -->

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url] [![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![Apache 2.0][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

<!-- PROJECT LOGO -->
<br />
<div align="center">
<a href="https://github.com/sovity/edc-extensions">
<img src="https://raw.githubusercontent.com/sovity/edc-ui/main/src/assets/images/sovity_logo.svg" alt="Logo" width="300">
</a>

<h3 align="center">sovity Community Edition EDC</h3>
<p align="center" style="padding-bottom:16px">
Extended EDC Connector by sovity.
<br />
<a href="https://github.com/sovity/edc-extensions/issues/new?template=bug_report.md">Report Bug</a>
·
<a href="https://github.com/sovity/edc-extensions/issues/new?template=feature_request.md">Request Feature</a>
</p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
   <summary>Table of Contents</summary>
   <ol>
      <li><a href="#about-the-project">About The Project</a></li>
      <li><a href="#our-edc-community-edition">sovity Community Edition EDC</a></li>
      <li><a href="#our-edc-extensions">sovity Community Edition EDC Extensions</a></li>
      <li><a href="#compatibility">Compatibility</a></li>
      <li><a href="#getting-started">Getting Started</a></li>
      <li><a href="#contributing">Contributing</a></li>
      <li><a href="#license">License</a></li>
      <li><a href="#contact">Contact</a></li>
   </ol>
</details>

<!-- ABOUT THE PROJECT -->

## About The Project

[Eclipse Dataspace Components](https://github.com/eclipse-edc) (EDC) is a framework
for building dataspaces, exchanging data securely with ensured data sovereignty.

[sovity](https://sovity.de/) extends the EDC Connector's functionality with extensions to offer
enterprise-ready managed services like "Connector-as-a-Service", out-of-the-box fully configured DAPS
and integrations to existing other dataspace technologies.

This repository contains our sovity Community Edition EDCs, containing pre-configured Open Source EDC Extensions.

Check out our [Getting Started Section](#getting-started) on how to run a local sovity Community Edition EDC.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- COMPATIBILITY -->

## sovity Community Edition EDC

Our sovity Community Edition EDC takes available Open Source EDC Extensions and combines them with our own
open source EDC Extensions from this repository to build ready-to-use EDC Docker Images.

See [here](./connector/launchers/README.md) for a list of our sovity Community Edition EDC Docker images.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## sovity Community Edition EDC Extensions

Feel free to explore and use our [EDC Extensions](./connector/extensions) with your EDC setup.

We packaged critical extensions for compatibility with our EDC UI and general usability features into
[sovity EDC Extensions Package](./connector/extensions/sovity-edc-extensions-package).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Compatibility

Our sovity Community Edition EDC and sovity Community Edition EDC Extensions are targeted to run with
our [sovity/edc-ui](https://github.com/sovity/edc-ui).

Our sovity Community Edition EDC will use the current EDC Milestone with a certain delay
to ensure reliability with a new release. Earlier releases currently are not supported, but will be
supported, once the base EDC has a reliable version.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED -->

## Getting Started

The fastest way to get started is our [Getting Started Guide](docs/getting-started/README.md)
which takes you through the steps of either starting a local [docker-compose.yaml](docker-compose.yaml) or deploying a
productive sovity EDC CE or MDS EDC CE Connector.

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- CONTRIBUTING -->

## Contributing

Contributions are what make the open source community such an amazing place to
learn, inspire, and create. Any contributions you make are **greatly
appreciated**.

If you have a suggestion that would improve this project, please fork the repo and
create a pull request. You can also simply open
a [feature request](https://github.com/sovity/edc-extensions/issues/new?template=feature_request.md). Don't forget to
leave the project a ⭐, if you like the effort put into this version!

Our contribution guideline can be found in [CONTRIBUTING.md](CONTRIBUTING.md).

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
https://img.shields.io/github/contributors/sovity/edc-extensions.svg?style=for-the-badge

[contributors-url]: https://github.com/sovity/edc-extensions/graphs/contributors

[forks-shield]:
https://img.shields.io/github/forks/sovity/edc-extensions.svg?style=for-the-badge

[forks-url]: https://github.com/sovity/edc-extensions/network/members

[stars-shield]:
https://img.shields.io/github/stars/sovity/edc-extensions.svg?style=for-the-badge

[stars-url]: https://github.com/sovity/edc-extensions/stargazers

[issues-shield]:
https://img.shields.io/github/issues/sovity/edc-extensions.svg?style=for-the-badge

[issues-url]: https://github.com/sovity/edc-extensions/issues

[license-shield]:
https://img.shields.io/github/license/sovity/edc-extensions.svg?style=for-the-badge

[license-url]: https://github.com/sovity/edc-extensions/blob/main/LICENSE

[linkedin-shield]:
https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555

[linkedin-url]: https://www.linkedin.com/company/sovity
