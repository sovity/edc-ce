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

- [ ] Release [edc-ui](https://github.com/sovity/edc-ui), this might require several steps, first of which is to [create a new `Release` issue](https://github.com/sovity/edc-ui/issues/new/choose)
- [ ] Decide a release version depending on major/minor/patch changes in the CHANGELOG.md.
- [ ] Update this issue's title to the new version
- [ ] `release-prep` PR:
    - [ ] Write or review the current [Productive Deployment Guide](https://github.com/sovity/edc-extensions/blob/main/docs/deployment-guide/goals/production)
    - [ ] Write or review the current [Development Deployment Guide](https://github.com/sovity/edc-extensions/blob/main/docs/deployment-guide/goals/development)
    - [ ] Write or review the current [Local Demo Deployment Guide](https://github.com/sovity/edc-extensions/blob/main/docs/deployment-guide/goals/local-demo)
    - [ ] For Major version updates: If we want to continue supporting the old major version:
        - [ ] Keep the old Productive Development Guide in a separate location.
            - [ ] Add a note to the old version about its deprecation status.
            - [ ] Add a Link the old version in the new version for discoverability.
            - [ ] Check all links in the old version.
        - [ ] Keep the old Productive Development Guide in a separate location.
            - [ ] Add a note to the old version about its deprecation status.
            - [ ] Add a Link the old version in the new version for discoverability.
            - [ ] Check all links in the old version.
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
        - [ ] Add a link to the EDC UI Release Deployment Migration Notes from the Deployment Migration section if the EDC UI has Deployment Migration Notes.
        - [ ] Remove empty sections from the patch notes.
    - [ ] Replace the existing `docker-compose.yaml` with `docker-compose-dev.yaml`.
    - [ ] Set the version for `EDC_IMAGE` of
      the [docker-compose's .env file](https://github.com/sovity/edc-extensions/blob/main/.env).
    - [ ] Set the version for `TEST_BACKEND_IMAGE` of
      the [docker-compose's .env file](https://github.com/sovity/edc-extensions/blob/main/.env).
    - [ ] Set the UI release version for `EDC_UI_IMAGE` of
      the [docker-compose's .env file](https://github.com/sovity/edc-extensions/blob/main/.env).
    - [ ] If the Eclipse EDC version changed, update
      the [eclipse-edc-management-api.yaml file](https://github.com/sovity/edc-extensions/blob/main/docs/eclipse-edc-management-api.yaml).
    - [ ] Update the Postman Collection if required.
    - [ ] Merge the `release-prep` PR.
- [ ] Wait for the main branch to be green.
- [ ] Test the release `docker-compose.yaml` with `RELEASE_EDC_IMAGE=ghcr.io/sovity/edc-dev:latest`.
    - [ ] Ensure with a `docker ps -a` that all containers are healthy, and not `healthy: starting` or `healthy: unhealthy`.
- [ ] Test the postman collection against that running docker-compose.
- [ ] Create a release and re-use the changelog section as release description, and the version as title.
- [ ] Check if the pipeline built the release versions in the Actions-Section (or you won't see it).
- [ ] Revisit the changed list of tasks and compare it
  with [.github/ISSUE_TEMPLATE/release.md](https://github.com/sovity/edc-extensions/blob/main/.github/ISSUE_TEMPLATE/release.md).
  Propose changes where it makes sense.
- [ ] Close this issue.
- [ ] Inform the Product Manager of this new release
