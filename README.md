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

<h3 align="center">EDC Extensions & EDC Community Edition</h3>
<p align="center" style="padding-bottom:16px">
Extended EDC Connector by sovity.
<br />
<a href="https://github.com/sovity/edc-extensions/issues">Report Bug</a>
·
<a href="https://github.com/sovity/edc-extensions/issues">Request Feature</a>
</p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
   <summary>Table of Contents</summary>
   <ol>
      <li><a href="#about-the-project">About The Project</a></li>
      <li><a href="#our-edc-community-edition">Our EDC Community Edition</a></li>
      <li><a href="#our-edc-extensions">Our EDC Extensions</a></li>
      <li><a href="#compatibility">Compatibility</a></li>
      <li><a href="#getting-started">Getting Started</a></li>
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

[sovity](https://sovity.de/) extends the EDC Connector's functionality with extensions to offer
enterprise-ready managed services like "Connector-as-a-Service", out-of-the-box fully configured DAPS
and integrations to existing other data space technologies.

This repository contains both our EDC Community Editions and our
EDC Extensions.

Check out our [Getting Started Section](#getting-started) on how to run a local copy.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- COMPATIBILITY -->

## Our EDC Community Edition

Our EDC Community Edition takes available EDC Open Source extensions and combines them with our own
open source extensions from this repository to build ready-to-use EDC Docker Images.

See [here](./connector/README.md) for a list of our EDC Community Edition Docker Images.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Our EDC Extensions

Feel free to explore our [EDC Extensions](./extensions).

Critical extensions for compatibility with our EDC UI or general usability we packaged into
our [Sovity EDC Extensions Package](./extensions/sovity-edc-extensions-package).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Compatibility

Our EDC Community Edition and extensions are targeted to run with
our [sovity/edc-ui](https://github.com/sovity/edc-ui).

Our extensions and EDC Community Edition will use the current EDC Milestone with a certain delay.

There is no support for past milestones, as there is no support for past EDC milestones.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED -->

## Getting Started

The fastest way to get started is our [Getting Started Guide](./docs/getting-started/README.md)
which takes you through the steops of configuring and starting our
[docker-compose.yaml](docker-compose.yaml).

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

[license-url]: https://github.com/sovity/edc-extensions/blob/master/LICENSE.txt

[linkedin-shield]:
https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555

[linkedin-url]: https://www.linkedin.com/company/sovity
