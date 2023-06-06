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
  - [ ] Update the CHANGELOG.md.
    - [ ] Add a clean `Unreleased` version.
    - [ ] Add the version to the old section.
    - [ ] Add the current date to the old version.
    - [ ] Write or review the `Deployment Migration Notes` section.
    - [ ] Write or review a release summary.
    - [ ] Remove empty sections from the patch notes.
  - [ ] Replace the existing `docker-compose.yaml` file from `docker-compose-dev.yaml`.
  - [ ] Remove the second connector from the `docker-compose.yaml` file.
  - [ ] Rename the `${DEV_*}` variables with their `${RELEASE_*` counterparts.
  - [ ] Set the version for `RELEASE_EDC_IMAGE` of the [docker-compose's .env file](https://github.com/sovity/edc-extensions/blob/main/.env).
  - [ ] Set the UI release version for `RELEASE_EDC_UI_IMAGE` of the [docker-compose's .env file](https://github.com/sovity/edc-extensions/blob/main/.env).
  - [ ] If the core EDC version changed, update the `openapi.yaml`.
  - [ ] Update the Postman Collection if required.
  - [ ] Merge the `release-prep` PR.
- [ ] Create a release and re-use the changelog section as release description, and the version as title.
- [ ] Check if the pipeline built the release versions in the Actions-Section (or you won't see it).
- [ ] Test the release `docker-compose.yaml`.
- [ ] Revisit the changed list of tasks and compare it with [.github/ISSUE_TEMPLATE/release.md](https://github.com/sovity/edc-extensions/blob/main/.github/ISSUE_TEMPLATE/bug_report.md). Propose changes where it
  makes sense.
