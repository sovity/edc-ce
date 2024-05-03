Updating the Changelog
======================

This project uses a [CHANGELOG.md](../../CHANGELOG.md).

## Structure of the Changelog

Each pull request should also update the "Unreleased" section of the changelog.
It should also update the "Deployment Migration Notes" Section of the unreleased section as preparation for the release.

For each release there will be a separate section especially with an "Overview" section containing a summary
from a product perspective.

Releases will especially contain a "Compatible Versions" section with the final docker
images and versions of other software components that are connected by APIs.

## How to categorize a change

The changelog uses [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
Changes are categorized as either Major, Minor or Patch Changes.

For this project, changes are categorized as the following:

### Major Changes

Major changes include:

- UX / Product overhauls.
- Breaking Changes in Connector-To-Connector communication
- Breaking Changes to the required deployment units.
- Breaking Changes in APIs for third party applications.

### Minor Changes

Minor changes include:

- New or changed features from a customer perspective.
- New APIs with API contracts with other deployment units (our UI doesn't count).
- New Product Documentation

### Patch Changes

Patch changes are basically everything else, that does not add, change or remove any product or external API features.

- Product Fixes, Bugfixes, Refactorings
- Changes to existing Product Documentation
- New or changes to existing Developer Documentation
- Everything else

## Released Versions

On releases the "Unreleased" section is emptied in favor of a new section for the release.

Whether a release will bump the major, minor or patch version is decided by the unreleased changes in the changelog.

The Release sections will be cleaned up on release, improved with additional information and made
useful for the customer and people deploying the application, containing both product changes and
deployment migration notes.

More on that can be found in the [Release Issue Template](../../.github/ISSUE_TEMPLATE/release.md).
