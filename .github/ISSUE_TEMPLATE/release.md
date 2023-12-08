---
name: Release
about: Create an issue to track a release process.
title: "Release vx.x.x"
labels: ["task/release", "scope/mds"]
assignees: ""
---

# Release

## Work Breakdown

Feel free to edit this release checklist in-progress depending on what tasks need to be done:

- [ ] Release [edc-ui](https://github.com/sovity/edc-ui), this might require several steps.
- [ ] Release [edc-extensions](https://github.com/sovity/edc-extensions), this might require several steps.
- [ ] Decide a release version depending on major/minor/patch changes in the CHANGELOG.md.
- [ ] Update this issue's title to the new version
- [ ] `release-prep` PR:
    - [ ] Update the CHANGELOG.md.
        - [ ] Add a clean `Unreleased` version.
        - [ ] Add the version to the old section.
        - [ ] Add the current date to the old version.
        - [ ] Write or review the `Deployment Migration Notes` section.
        - [ ] Ensure the `Deployment Migration Notes` contains the compatible docker images.
        - [ ] Write or review a release summary.
        - [ ] Remove empty sections from the patch notes.
    - [ ] Update the [gradle.properties](https://github.com/sovity/edc-broker-server-extension/blob/main/gradle.properties) to contain the released edc-extensions version.
    - [ ] Set the broker server release version in the [docker-compose's .env file](https://github.com/sovity/edc-broker-server-extension/blob/main/.env).
    - [ ] Set the EDC UI release version in the [docker-compose's .env file](https://github.com/sovity/edc-broker-server-extension/blob/main/.env).
    - [ ] Set the EDC CE release version in the [docker-compose's .env file](https://github.com/sovity/edc-broker-server-extension/blob/main/.env).
    - [ ] Merge the `release-prep` PR.
- [ ] Wait for the main branch to be green.
- [ ] Test the `docker-compose.yaml` with `BROKER_IMAGE=ghcr.io/sovity/broker-server-dev:main`.
- [ ] Create a release and re-use the changelog section as release description, and the version as title.
- [ ] Check if the pipeline built the release versions in the [Actions-Section](https://github.com/sovity/edc-broker-server-extension/actions?query=event%3Arelease) (or you won't see it).
- [ ] Checkout the release tag and check test the `docker-compose.yaml`.
  - [ ] Ensure with a `docker ps -a` that all containers are healthy, and not `healthy: starting` or `healthy: unhealthy`. 
- [ ] Check the contents of the Deployment Docs Zip from the GitHub Release.
- [ ] Send out a release notification E-Mail to the MDS, the MDS integrator company and the MDS operator company.
    - [ ] Check @jkbquabeck for an up-to-date mailing list, separated into "To" and "Cc".
    - [ ] Attach the Deployment Docs Zip generated during the GitHub release, which should now contain the CHANGELOG, deployment migration notes, an initial deployment guide and a local demo docker compose.
- [ ] Optional, this can be done mid-development if required:
    - [ ] Create a `release-cleanup` PR. 
    - [ ] Revert the versions in the [docker-compose's .env file](.env) back to latest for the EDC UI.
    - [ ] Revert the versions in the [docker-compose's .env file](.env) back to latest for the EDC CE.
    - [ ] Revert the versions in the [docker-compose's .env file](.env) back to latest for the Broker Server.
    - [ ] Update the [gradle.properties](https://github.com/sovity/edc-broker-server-extension/blob/main/gradle.properties) to contain the edc-extensions version `0.0.1-SNAPSHOT`.
    - [ ] Merge the `release-cleanup` PR.
- [ ] Revisit the changed list of tasks and compare it with [.github/ISSUE_TEMPLATE/release.md](https://github.com/sovity/edc-broker-server-extension/blob/main/.github/ISSUE_TEMPLATE/release.md). Apply changes where it makes sense.
- [ ] Close this issue.
