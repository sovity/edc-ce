---
name: Release
about: Create an issue to track a release process.
title: "Release x.x.x"
labels: ["task/release", "scope/ce"]
assignees: ""
---

# Release

## Work Breakdown

Feel free to edit this release checklist in-progress depending on what tasks need to be done:

- [ ] Release [edc-ui](https://github.com/sovity/edc-ui), this might require several steps.
- [ ] Decide a release version depending on major/minor/patch changes in the CHANGELOG.md.
- [ ] Update this issue's title to the new version
- [ ] `release-prep` PR:
    - [ ] Write or review the current [Productive Deployment Guide](/docs/deployment-guide/goals/production)
    - [ ] Write or review the current [Development Deployment Guide](/docs/deployment-guide/goals/development)
    - [ ] Write or review the current [Local Demo Deployment Guide](/docs/deployment-guide/goals/local-demo)
    - [ ] Update the CHANGELOG.md.
        - [ ] Add a clean `Unreleased` version.
        - [ ] Add the version to the old section.
        - [ ] Add the current date to the old version.
        - [ ] Check the commit history for commits that might be product-relevant and thus should be added to the
          changelog. Maybe they were forgotten.
        - [ ] Write or review the `Deployment Migration Notes` section, check the commit history for changed / added
          configuration properties.
        - [ ] Write or review a release summary.
        - [ ] Write or review the compatible versions section.
        - [ ] Add a link to the EDC UI Release to the "EDC UI" section.
        - [ ] Remove empty sections from the patch notes.
    - [ ] Replace the existing `docker-compose.yaml` with `docker-compose-dev.yaml`.
    - [ ] Set the version for `EDC_IMAGE` of
      the [docker-compose's .env file](https://github.com/sovity/edc-extensions/blob/main/.env).
    - [ ] Set the version for `TEST_BACKEND_IMAGE` of
      the [docker-compose's .env file](https://github.com/sovity/edc-extensions/blob/main/.env).
    - [ ] Set the UI release version for `EDC_UI_IMAGE` of
      the [docker-compose's .env file](https://github.com/sovity/edc-extensions/blob/main/.env).
    - [ ] If the core EDC version changed, update the `openapi.yaml`.
    - [ ] Update the Postman Collection if required.
    - [ ] Merge the `release-prep` PR.
- [ ] Wait for the main branch to be green.
- [ ] Test the release `docker-compose.yaml` with `RELEASE_EDC_IMAGE=ghcr.io/sovity/edc-dev:latest`.
- [ ] Test the postman collection against that running docker-compose.
- [ ] Create a release and re-use the changelog section as release description, and the version as title.
- [ ] Check if the pipeline built the release versions in the Actions-Section (or you won't see it).
- [ ] Revisit the changed list of tasks and compare it
  with [.github/ISSUE_TEMPLATE/release.md](https://github.com/sovity/edc-extensions/blob/main/.github/ISSUE_TEMPLATE/release.md).
  Propose changes where it
  makes sense.
- [ ] Close this issue.
