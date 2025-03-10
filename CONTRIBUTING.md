# Contributing to the Project

Thank you for your interest in contributing to this project.

Before you commit yourself to contributing to this project, we would like to inform you about the steps and guardrails for contributions.

## Product Fit

We are a SaaS startup and thus to some capacity forced to be selective in what we do and what we choose to maintain. 

By our experience, maintenance efforts over the ever-changing EDC Connector versions and their accompanying paradigm shifts are multiple times the development effort. Effort, that we need to make time for. This includes integration tests, E2E tests, documentation, support and the general harmonization of any feature into the entirety of our product and consulting efforts over time.

Because of this, your best chance to contribute a **feature** would be through early close collaboration with us, with the opportunity for you to convince us of your vision, as only a good product vision alignment would ensure a feature survives.

Our goal remains a well-rounded production-ready industry-grade product with features fulfilling real-world demands and requirements. Thus, we have an incentive to take your pains and ideas seriously.

## Developer Experience

Our sovity Community Edition EDC is currently being released with our sovity Enterprise Edition EDC with the help of automation. While this brings multiple benefits to our internal development processes, such as a shorter development cycles, faster releases, a more stable product by well-integrated compatibility testing and aligned component versions, this requires contributing to be a rather precise process:

- The sovity Community Edition EDC does not include all tooling, e.g. E2E Test and Performance testing tooling.
- Unit Tests are mostly still in our Enterprise Edition. For contributions, integration tests might also "disappear" at release, when they have been moved into the correct places in the EE to be used for testing the feature in all variants and versions with each other. This entire process is still under construction. The Apache 2.0 license on the contributed files remains, and the file might re-appear in the future, when we have overhauled this process.
- Please plan enough time for the PR process. It is challenging to be as involved into a project as a contributor, compared to being onboarded onto the project.
- Please enable commits by maintainers for PRs, so we can fix e.g. CHANGELOG entries when wanting to merge a contribution before a release.

## Contributor License Agreement

- The sovity Community Edition EDC is distributed under the [Elastic License 2.0](licenses/ELASTIC-LICENSE-2.0.md).
- Source code contributions, however, are licensed under [Apache License 2.0](licenses/APACHE-LICENSE-2.0.md), ensuring you keep your rights to your source code.
- See [LICENSE](LICENSE) for more information.
- This is detailed by our **Contributor License Agreement (CLA)**, which you will have to sign on your first PR.

## Contact

For feature requests you can open a "Product: Feature Request" issue [here](https://github.com/sovity/edc-ce/issues/new?template=feature_request.md).

For collaboration requests feel free to contact us under contact@sovity.de
