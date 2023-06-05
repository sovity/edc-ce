---
name: Release
about: Create an issue to track a release process.
title: "Release x.x.x"
labels: ["task/release", "scope/mds"]
assignees: ""
---

# Release

## Work Breakdown

Feel free to edit this release checklist in-progress depending on what tasks need to be done:

- [ ] Release [edc-ui](https://github.com/sovity/edc-ui), this might require several steps.
- [ ] Release [edc-extensions](https://github.com/sovity/edc-extensions), this might require several steps.
- [ ] Update the CHANGELOG.md.
    - [ ] Decide a release version depending on major/minor/patch changes.
    - [ ] Add a clean `Unreleased` version and rename the old section to the release version.
    - [ ] Remove empty sections from the release version's patch notes.
    - [ ] Write or review the `Deployment Migration Notes` section.
    - [ ] Write or review a release summary.
- [ ] Set the release versions in the [docker-compose's .env file](.env).
- [ ] Set the release versions in the [Deployment Section of our README.md](README.md#deployment). Use text instead of a
  link for the broker server version, as the package version does only exist after the release.
- [ ] Commit those changes in a `release-prep` PR.
- [ ] Manually test the release.
- [ ] Create a release and re-use the changelog section as release description.
- [ ] Check if the pipeline built the release versions in the Actions-Section (or you won't see it).
- [ ] Revert the versions in the [docker-compose's .env file](.env) back to the snapshot/nightly versions of the
  components.
- [ ] Change the broker server release version references in
  the [Deployment Section of our README.md](README.md#deployment) to links to the package version.
- [ ] Change the broker server release version references in the Release Description to links to the package version.
- [ ] Commit those changes in a `release-cleanup` PR.
- [ ] Revisit the changed list of tasks and compare it with [.github/ISSUE_TEMPLATE/release.md]. Apply changes where it
  makes sense.
- [ ] Notify the deployment team with the release notes, which should now contain both product changes and a
  configuration migration guide.